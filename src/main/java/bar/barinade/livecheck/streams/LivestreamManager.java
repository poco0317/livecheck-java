package bar.barinade.livecheck.streams;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import bar.barinade.livecheck.discord.BotManager;
import bar.barinade.livecheck.discord.serverconfig.data.ServerConfiguration;
import bar.barinade.livecheck.discord.serverconfig.service.BlacklistedCategoryService;
import bar.barinade.livecheck.discord.serverconfig.service.BlacklistedChannelService;
import bar.barinade.livecheck.discord.serverconfig.service.DefinedCategoryService;
import bar.barinade.livecheck.discord.serverconfig.service.DefinedChannelService;
import bar.barinade.livecheck.discord.serverconfig.service.ServerConfigService;
import bar.barinade.livecheck.discord.serverconfig.service.WhitelistedCategoryService;
import bar.barinade.livecheck.discord.serverconfig.service.WhitelistedChannelService;
import bar.barinade.livecheck.streams.data.LivestreamInfo;
import bar.barinade.livecheck.streams.data.PostedLivestream;
import bar.barinade.livecheck.streams.data.pk.PostedLivestreamId;
import bar.barinade.livecheck.streams.data.repo.LivestreamInfoRepo;
import bar.barinade.livecheck.streams.data.repo.PostedLivestreamRepo;
import bar.barinade.livecheck.streams.twitch.TwitchLivestreamImpl;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

@Service
public class LivestreamManager {
	
	private static final Logger m_logger = LoggerFactory.getLogger(LivestreamManager.class);
	
	private static final long AGGREGATION_LOOP_WAIT_MILLIS = 1000L * 60L * 5L; // run aggregate every 5 minutes
	private static final long INITIAL_WAIT_MILLIS = 1000L * 10L; // wait 10 seconds after startup before going
	
	private static ConcurrentHashMap<LivestreamImpl.Platform, LivestreamImpl> streamApis = new ConcurrentHashMap<>(); 

	@Autowired
	private ApplicationContext springContext;
	
	@Autowired
	private ServerConfigService configService;
	@Autowired
	private BlacklistedCategoryService blCategoryService;
	@Autowired
	private BlacklistedChannelService blChannelService;
	@Autowired
	private WhitelistedCategoryService wlCategoryService;
	@Autowired
	private WhitelistedChannelService wlChannelService;
	@Autowired
	private DefinedCategoryService categoryService;
	@Autowired
	private DefinedChannelService channelService;
	
	@Autowired
	private BotManager botManager;
	
	@Autowired
	private LivestreamInfoRepo streamRepo;
	@Autowired
	private PostedLivestreamRepo postRepo;
	
	@PostConstruct
	public void init() {
		m_logger.info("Initializing LivestreamManager");
		
		streamApis.clear();
		streamApis.put(LivestreamImpl.Platform.TWITCH, springContext.getBean(TwitchLivestreamImpl.class));
		
		m_logger.info("LivestreamManager initialize finished");
	}
	
	/**
	 * Collect all streams for all servers and update appropriately.
	 * This function normally should be run on a loop, doing most of the work.
	 */
	@Scheduled(fixedDelay = AGGREGATION_LOOP_WAIT_MILLIS, initialDelay = INITIAL_WAIT_MILLIS)
	public void aggregate() {
		m_logger.info("Beginning global livestream update");
		JDA jda = botManager.getJDA();
		
		// collect all running guild ids (if kicked from a server there's no chance of fixing the messages)
		List<Long> guildIds = new ArrayList<>();
		jda.getGuilds().forEach(guild -> guildIds.add(guild.getIdLong()));
		
		// all configured categories/games and streamers
		// gotta go fast
		Set<String> allCategories = new HashSet<>();
		Set<String> allChannels = new HashSet<>();
		HashMap<Long, HashSet<String>> guildsToChannels = new HashMap<>();
		HashMap<Long, HashSet<String>> guildsToCategories = new HashMap<>();
		for (final Long id : guildIds) {
			HashSet<String> channels = new HashSet<>();
			HashSet<String> categories = new HashSet<>();
			channelService.getAll(id).forEach(channel -> {
				allChannels.add(channel.getId().getChannel());
				channels.add(channel.getId().getChannel());
			});
			categoryService.getAll(id).forEach(category -> {
				allCategories.add(category.getId().getCategory());
				categories.add(category.getId().getCategory());
			});
			guildsToChannels.put(id, channels);
			guildsToCategories.put(id, categories);
		}
		List<String> allCategoriesList = new ArrayList<>(allCategories);
		List<String> allChannelsList = new ArrayList<>(allChannels);
		
		// all currently live found streams
		HashMap<LivestreamImpl.Platform, HashMap<String, LivestreamInfo>> liveStreamsByName = new HashMap<>();
		HashMap<LivestreamImpl.Platform, HashMap<String, LivestreamInfo>> liveStreamsByCategory = new HashMap<>();
		for (LivestreamImpl impl : streamApis.values()) {
			List<LivestreamInfo> live = impl.getLivestreams(allCategoriesList, allChannelsList);
			HashMap<String, LivestreamInfo> byName = new HashMap<>();
			HashMap<String, LivestreamInfo> byCategory = new HashMap<>();
			live.forEach(info -> {
				byName.put(info.getId().getName(), info);
				byCategory.put(info.getCategory(), info);
			});
			liveStreamsByName.put(impl.getPlatform(), byName);
			liveStreamsByCategory.put(impl.getPlatform(), byCategory);
		}
		
		// for each guild ... update, delete, or create messages
		for (final Long id : guildIds) {
			final Guild guild = jda.getGuildById(id);
			if (guild == null) {
				postRepo.deleteByIdGuildId(id);
				m_logger.info("Guild {} was missing when preparing to send messages. Purged leftover posts from db", id);
				continue;
			}
			
			Long outputChannelId = configService.getOutputChannel(id);
			if (outputChannelId == null) {
				postRepo.deleteByIdGuildId(id);
				m_logger.info("Guild {} output channel was found to be null. Purging leftover posts from db", id);
				continue;
			}
			
			// set up the configured things for this guild
			HashSet<String> blacklistedCategories = new HashSet<>();
			HashSet<String> blacklistedChannels = new HashSet<>();
			HashSet<String> whitelistedCategories = new HashSet<>();
			HashSet<String> whitelistedChannels = new HashSet<>();
			String titleregex = configService.getRequiredTitleRegex(id);
			
			blCategoryService.getAll(id).forEach(category -> blacklistedCategories.add(category.getId().getCategory()));
			wlCategoryService.getAll(id).forEach(category -> whitelistedCategories.add(category.getId().getCategory()));
			blChannelService.getAll(id).forEach(channel -> blacklistedChannels.add(channel.getId().getChannel()));
			wlChannelService.getAll(id).forEach(channel -> whitelistedChannels.add(channel.getId().getChannel()));
			ServerConfiguration config = configService.getConfig(id);
			Long liveMentionRole = configService.getMentionRole(id);
			
			HashMap<LivestreamImpl.Platform, HashSet<String>> streamersAlreadyChecked = new HashMap<>();
			for (LivestreamImpl impl : streamApis.values()) {
				streamersAlreadyChecked.put(impl.getPlatform(), new HashSet<String>());
			}
			
			// determine if a post should be updated or deleted and do the appropriate action
			List<PostedLivestream> currentPosts = postRepo.findByIdGuildId(id);
			for (PostedLivestream post : currentPosts) {
				final TextChannel txtchan = guild.getTextChannelById(post.getChannelId());
				if (txtchan == null) {
					postRepo.delete(post);
					m_logger.info("Guild {} channel {} disappeared. Purged associated message from db", id, post.getChannelId());
					continue;
				}
				
				Message msg = null;
				
				// insanely scuffed hack for when messages disappear
				try {
					msg = txtchan.retrieveMessageById(post.getId().getMessageId()).complete();
				} catch (Exception e) {}
				if (msg == null) {
					postRepo.delete(post);
					m_logger.info("Guild {} message {} disappeared. Purged associated message from db", id, post.getId().getMessageId());
					continue;
				}
				
				final LivestreamInfo postInfo = post.getId().getInfo();
				final String username = postInfo.getId().getName();
				final String category = postInfo.getCategory();
				final LivestreamImpl.Platform platform = postInfo.getId().getPlatform();
				
				HashMap<String, LivestreamInfo> channelsLiveForThisPlatform = liveStreamsByName.get(platform);
				String streamTitle = postInfo.getTitle() != null ? String.format("\"%s\"", postInfo.getTitle()) : "(blank title)";
				boolean regexmatch = true;
				if (titleregex != null && !titleregex.isEmpty()) {
					try {
						Pattern p = Pattern.compile(titleregex);
						Matcher m = p.matcher(streamTitle);
						regexmatch = m.matches();
					} catch (Exception e) {
						regexmatch = false;
						m_logger.info("Bad regex pattern passed for guild {}: '{}'", id, titleregex);
					}
				}
				
				// delete a post if it fails whitelist check, blacklist check, or literally isnt live at all
				boolean deleteThisPost = channelsLiveForThisPlatform == null || channelsLiveForThisPlatform.get(username) == null
						|| (!(guildsToChannels.get(id) != null && guildsToChannels.get(id).contains(username))
						&& !(guildsToCategories.get(id) != null && guildsToCategories.get(id).contains(category)))
						|| (blacklistedChannels != null && blacklistedChannels.contains(username))
						|| (blacklistedCategories != null && blacklistedCategories.contains(category))
						|| (whitelistedCategories != null && whitelistedCategories.size() > 0 && !whitelistedCategories.contains(category))
						|| (whitelistedChannels != null && whitelistedChannels.size() > 0 && !whitelistedChannels.contains(username))
						|| (titleregex != null && !regexmatch);
				
				if (deleteThisPost) {
					// delete
					m_logger.debug("Deleted stream post '{}' | Guild {}", post.getId().getMessageId(), id);
					msg.delete().queue();
					postRepo.delete(post);
				} else {
					// edit
					final LivestreamInfo newInfoInUse = channelsLiveForThisPlatform.get(username);
					final MessageEmbed embed = generateEmbedForInfo(newInfoInUse, streamApis.get(platform));
					msg.editMessageEmbeds(embed).queue();
					postRepo.save(post);
					streamRepo.save(newInfoInUse);
					streamersAlreadyChecked.get(platform).add(username);
				}
			}
			
			// flush edited and deleted posts
			streamRepo.flush();
			postRepo.flush();
			
			// new posts
			for (LivestreamImpl impl : streamApis.values()) {
				final LivestreamImpl.Platform platform = impl.getPlatform();
				final HashSet<String> streamersToSkip = streamersAlreadyChecked.get(platform);
				for (LivestreamInfo info : liveStreamsByName.get(platform).values()) {
					
					// this stream was live before and had its embed edited, dont need to do anything
					if (streamersToSkip.contains(info.getId().getName())) {
						continue;
					}
					
					final TextChannel txtchan = guild.getTextChannelById(outputChannelId);
					if (txtchan == null) {
						m_logger.info("Guild {} channel {} disappeared before new post was made...", id);
						continue;
					}
					
					final String username = info.getId().getName();
					final String category = info.getCategory();
					String streamTitle = info.getTitle() != null ? String.format("\"%s\"", info.getTitle()) : "(blank title)";
					boolean regexmatch = true;
					if (titleregex != null && !titleregex.isEmpty()) {
						try {
							Pattern p = Pattern.compile(titleregex);
							Matcher m = p.matcher(streamTitle);
							regexmatch = m.matches();
						} catch (Exception e) {
							regexmatch = false;
							m_logger.info("Bad regex pattern passed for guild {}: '{}'", id, titleregex);
						}
					}
					// dont post if it fails whitelist check, blacklist check, or literally isnt live at all
					boolean shouldNotPostThis = (!(guildsToChannels.get(id) != null && guildsToChannels.get(id).contains(username))
							&& !(guildsToCategories.get(id) != null && guildsToCategories.get(id).contains(category)))
							|| (blacklistedChannels != null && blacklistedChannels.contains(username))
							|| (blacklistedCategories != null && blacklistedCategories.contains(category))
							|| (whitelistedCategories != null && whitelistedCategories.size() > 0 && !whitelistedCategories.contains(category))
							|| (whitelistedChannels != null && whitelistedChannels.size() > 0 && !whitelistedChannels.contains(username))
							|| (titleregex != null && !regexmatch);
					
					if (shouldNotPostThis) {
						m_logger.trace("skipped a stream - GUILD {} | user {} | category {}", id, username, category);
						continue;
					}
					
					PostedLivestream newPost = new PostedLivestream();
					PostedLivestreamId postId = new PostedLivestreamId();
					postId.setInfo(info);
					postId.setGuild(config);
					newPost.setChannelId(outputChannelId);
					
					final MessageEmbed embed = generateEmbedForInfo(info, impl);
					
					MessageBuilder builder = new MessageBuilder()
							.setEmbeds(embed);
					
					// mention when any streamer goes live
					if (liveMentionRole != null) {
						final Role role = guild.getRoleById(liveMentionRole);
						if (role != null) {
							builder = builder.setContent(role.getAsMention()).mentionRoles(liveMentionRole);
						}
					}
					
					Message toSend = builder.build();
					txtchan.sendMessage(toSend).queue(sentMessage -> {
						postId.setMessageId(sentMessage.getIdLong());
						newPost.setId(postId);
						streamRepo.saveAndFlush(info);
						postRepo.saveAndFlush(newPost);
						m_logger.debug("Successfully sent new live message: Guild {} | Message {}", id, sentMessage.getId());
					});
				}
			}
		}
		
		streamRepo.flush();
		postRepo.flush();
		m_logger.info("Finished global livestream update");
	}
	
	private MessageEmbed generateEmbedForInfo(LivestreamInfo newInfo, LivestreamImpl impl) {
		final String xPlayingY = String.format("%s playing %s", newInfo.getId().getName(), newInfo.getCategory());
		final String streamUrl = impl.getStreamUrl(newInfo);
		JDA jda = botManager.getJDA();
		
		// be careful to not let nulls get in the way
		String followers = newInfo.getFollowers() != null ? String.format("%d", newInfo.getFollowers()) : "unknown";
		String totViews = newInfo.getTotalViews() != null ? String.format("%d", newInfo.getTotalViews()) : "unknown";
		String curViews = newInfo.getCurrentViewers() != null ? String.format("%d", newInfo.getCurrentViewers()) : "unknown";
		String status = newInfo.getStatus() != null && !newInfo.getStatus().isEmpty() ? StringUtils.capitalize(newInfo.getStatus()) : "Non-Affiliate";
		String streamTitle = newInfo.getTitle() != null ? String.format("\"%s\"", newInfo.getTitle()) : "(blank title)";
		String description = newInfo.getDescription() != null ? newInfo.getDescription() : "No description";
		
		return new EmbedBuilder()
				.setTitle(xPlayingY, streamUrl)
				.setDescription(streamTitle)
				.setColor(0x71368a) // dark purple
				.setTimestamp(Instant.now())
				.setAuthor("Live on " + impl.getPlatform().stylizedName())
				.setFooter(impl.getPlatform().stylizedName(), jda.getSelfUser().getEffectiveAvatarUrl())
				.setImage(newInfo.getThumbnailUrl())
				.setThumbnail(newInfo.getAvatarUrl())
				.addField("Followers", followers, true)
				.addField("Total Views", totViews, true)
				.addField("Current Views", curViews, true)
				.addField("Status", status, true)
				.addField("Description", description, true)
				.build();
		
	}
}
