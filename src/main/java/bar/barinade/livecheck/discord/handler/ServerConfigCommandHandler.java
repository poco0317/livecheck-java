package bar.barinade.livecheck.discord.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import bar.barinade.livecheck.discord.serverconfig.data.BlacklistedCategory;
import bar.barinade.livecheck.discord.serverconfig.data.BlacklistedChannel;
import bar.barinade.livecheck.discord.serverconfig.data.DefinedCategory;
import bar.barinade.livecheck.discord.serverconfig.data.DefinedChannel;
import bar.barinade.livecheck.discord.serverconfig.data.WhitelistedCategory;
import bar.barinade.livecheck.discord.serverconfig.data.WhitelistedChannel;
import bar.barinade.livecheck.discord.serverconfig.service.BlacklistedCategoryService;
import bar.barinade.livecheck.discord.serverconfig.service.BlacklistedChannelService;
import bar.barinade.livecheck.discord.serverconfig.service.DefinedCategoryService;
import bar.barinade.livecheck.discord.serverconfig.service.DefinedChannelService;
import bar.barinade.livecheck.discord.serverconfig.service.ServerConfigService;
import bar.barinade.livecheck.discord.serverconfig.service.WhitelistedCategoryService;
import bar.barinade.livecheck.discord.serverconfig.service.WhitelistedChannelService;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

@Component
@Scope("prototype")
public class ServerConfigCommandHandler extends CommandHandlerBase {
	
	private static final Logger m_logger = LoggerFactory.getLogger(ServerConfigCommandHandler.class);
	
	private static final String BASE_CMD_NAME = "config";
	private static final String GROUPCMD_NAME_TXTCHAN = "channel";
	private static final String GROUPCMD_NAME_REQUIREDTITLE = "requiredtitle";
	private static final String GROUPCMD_NAME_CATEGORY = "category";
	private static final String GROUPCMD_NAME_CATEGORYWHITELIST = "category_wl";
	private static final String GROUPCMD_NAME_CATEGORYBLACKLIST = "category_bl";
	private static final String GROUPCMD_NAME_STREAMER = "streamer";
	private static final String GROUPCMD_NAME_STREAMERWHITELIST = "streamer_wl";
	private static final String GROUPCMD_NAME_STREAMERBLACKLIST = "streamer_bl";
	private static final String GROUPCMD_NAME_MENTIONROLE = "mentionrole";
	private static final String SUBCMD_ADD = "add";
	private static final String SUBCMD_REMOVE = "remove";
	private static final String SUBCMD_SET = "set";
	private static final String SUBCMD_VIEW = "view";
	private static final String SUBCMD_DELALL = "delall";
	private static final String OPTION_CHANNEL = "channel";
	private static final String OPTION_STREAMER = "streamer";
	private static final String OPTION_CATEGORY = "category";
	private static final String OPTION_REGEX = "regex";
	private static final String OPTION_ROLE = "role";
	
	private static final String EXPLAIN_STREAMER_WHITELIST = "If in use, all streamers must be in this list, if detected via category.";
	private static final String EXPLAIN_CATEGORY_WHITELIST = "If in use, all categories must be in this list, if detected via streamer.";
	private static final String EXPLAIN_STREAMER_BLACKLIST = "If in use, any streamer found in this list will not show up when live.";
	private static final String EXPLAIN_CATEGORY_BLACKLIST = "If in use, any streamer with a category in this list will not show up.";
	
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
	
	@Override
	public CommandData[] getCommandsToUpsert() {
		// oh my god
		return new CommandData[] {
				new CommandData(BASE_CMD_NAME, "Modify Livecheck configuration for this server.")
				.addSubcommandGroups(
						new SubcommandGroupData(GROUPCMD_NAME_REQUIREDTITLE, "Modify the required title Regular Expression.")
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove the required title Regular Expression."),
								new SubcommandData(SUBCMD_SET, "Set the required title Regular Expression. If set, all stream titles must match.")
									.addOption(OptionType.STRING, OPTION_REGEX, "Regular Expression to match to stream titles", true),
								new SubcommandData(SUBCMD_VIEW, "View the required title Regular Expression.")
						),
						new SubcommandGroupData(GROUPCMD_NAME_TXTCHAN, "Modify the output channel for livestreams in this server.")
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Unset the output channel for livestreams."),
								new SubcommandData(SUBCMD_SET, "Set the output channel for livestreams.")
									.addOption(OptionType.CHANNEL, OPTION_CHANNEL, "Output channel for livestreams", true),
								new SubcommandData(SUBCMD_VIEW, "View the output channel for livestreams.")
						),
						new SubcommandGroupData(GROUPCMD_NAME_CATEGORY, "Add categories to or remove categories from the watchlist.")
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove a category from the watchlist.")
									.addOption(OptionType.STRING, OPTION_CATEGORY, "Category to remove", true),
								new SubcommandData(SUBCMD_ADD, "Add a category to the watchlist.")
									.addOption(OptionType.STRING, OPTION_CATEGORY, "Category to add", true),
								new SubcommandData(SUBCMD_VIEW, "View the list of categories being watched."),
								new SubcommandData(SUBCMD_DELALL, "Remove all categories from the watchlist.")
						),
						new SubcommandGroupData(GROUPCMD_NAME_STREAMER, "Add streamers to or remove streamers from the watchlist.")
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove a streamer from the watchlist.")
									.addOption(OptionType.STRING, OPTION_STREAMER, "Streamer to remove", true),
								new SubcommandData(SUBCMD_ADD, "Add a streamer to the watchlist.")
									.addOption(OptionType.STRING, OPTION_STREAMER, "Streamer to add", true),
								new SubcommandData(SUBCMD_VIEW, "View the list of streamers being watched."),
								new SubcommandData(SUBCMD_DELALL, "Remove all streamers from the watchlist.")
						),
						new SubcommandGroupData(GROUPCMD_NAME_STREAMERWHITELIST, EXPLAIN_STREAMER_WHITELIST)
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove from whitelist: "+EXPLAIN_STREAMER_WHITELIST)
									.addOption(OptionType.STRING, OPTION_STREAMER, "Streamer to remove", true),
								new SubcommandData(SUBCMD_ADD, "Add to whitelist: "+EXPLAIN_STREAMER_WHITELIST)
									.addOption(OptionType.STRING, OPTION_STREAMER, "Streamer to add", true),
								new SubcommandData(SUBCMD_VIEW, "View the whitelist: "+EXPLAIN_STREAMER_WHITELIST),
								new SubcommandData(SUBCMD_DELALL, "Remove all streamers from the whitelist.")
						),
						new SubcommandGroupData(GROUPCMD_NAME_STREAMERBLACKLIST, EXPLAIN_STREAMER_BLACKLIST)
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove from blacklist: "+EXPLAIN_STREAMER_BLACKLIST)
									.addOption(OptionType.STRING, OPTION_STREAMER, "Streamer to remove", true),
								new SubcommandData(SUBCMD_ADD, "Add to blacklist: "+EXPLAIN_STREAMER_BLACKLIST)
									.addOption(OptionType.STRING, OPTION_STREAMER, "Streamer to add", true),
								new SubcommandData(SUBCMD_VIEW, "View the blacklist: "+EXPLAIN_STREAMER_BLACKLIST),
								new SubcommandData(SUBCMD_DELALL, "Remove all streamers from the blacklist.")
						),
						new SubcommandGroupData(GROUPCMD_NAME_CATEGORYWHITELIST, EXPLAIN_CATEGORY_WHITELIST)
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove from whitelist: "+EXPLAIN_CATEGORY_WHITELIST)
									.addOption(OptionType.STRING, OPTION_CATEGORY, "Category to add", true),
								new SubcommandData(SUBCMD_ADD, "Add to whitelist: "+EXPLAIN_CATEGORY_WHITELIST)
									.addOption(OptionType.STRING, OPTION_CATEGORY, "Category to add", true),
								new SubcommandData(SUBCMD_VIEW, "View the whitelist: "+EXPLAIN_CATEGORY_WHITELIST),
								new SubcommandData(SUBCMD_DELALL, "Remove all categories from the whitelist.")
						),
						new SubcommandGroupData(GROUPCMD_NAME_CATEGORYBLACKLIST, EXPLAIN_CATEGORY_BLACKLIST)
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove from blacklist: "+EXPLAIN_CATEGORY_BLACKLIST)
									.addOption(OptionType.STRING, OPTION_CATEGORY, "Category to add", true),
								new SubcommandData(SUBCMD_ADD, "Add to blacklist: "+EXPLAIN_CATEGORY_BLACKLIST)
									.addOption(OptionType.STRING, OPTION_CATEGORY, "Category to add", true),
								new SubcommandData(SUBCMD_VIEW, "View the blacklist: "+EXPLAIN_CATEGORY_BLACKLIST),
								new SubcommandData(SUBCMD_DELALL, "Remove all categories from the blacklist.")
						),
						new SubcommandGroupData(GROUPCMD_NAME_MENTIONROLE, "Modify the role which is mentioned when any streamer goes live.")
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove the mention that occurs every time a streamer goes live."),
								new SubcommandData(SUBCMD_SET, "Set the role which is mentioned every time a streamer goes live.")
									.addOption(OptionType.ROLE, OPTION_ROLE, "Role to mention"),
								new SubcommandData(SUBCMD_VIEW, "View the role which is mentioned every time a streamer goes live.")
						)
				),
		};
	}
	
	void cmd_config(SlashCommandEvent event) {
		final String cmd = event.getName();
				
		if (cmd.equalsIgnoreCase(BASE_CMD_NAME)) {
			final String group = event.getSubcommandGroup();
			if (group != null) {
				switch (group) {
				case GROUPCMD_NAME_REQUIREDTITLE:
					reqTitle(event);
					break;
				case GROUPCMD_NAME_TXTCHAN:
					txtChannel(event);
					break;
				case GROUPCMD_NAME_CATEGORY:
					categoryWatch(event);
					break;
				case GROUPCMD_NAME_STREAMER:
					streamerWatch(event);
					break;	
				case GROUPCMD_NAME_STREAMERWHITELIST:
					wlStreamer(event);
					break;
				case GROUPCMD_NAME_STREAMERBLACKLIST:
					blStreamer(event);
					break;
				case GROUPCMD_NAME_CATEGORYWHITELIST:
					wlCategory(event);
					break;
				case GROUPCMD_NAME_CATEGORYBLACKLIST:
					blCategory(event);
					break;
				case GROUPCMD_NAME_MENTIONROLE:
					mentionRole(event);
					break;
				default:
					m_logger.warn("{} attempted to use unknown group {}", event.getUser().getId(), group);
					event.getHook().editOriginal("Invalid command group. Report to developer").queue();
					break;
				}
			}
		} else {
			
		}
	}
	
	private void reqTitle(SlashCommandEvent event) {
		final String method = event.getSubcommandName();
		if (method == null) {
			event.getHook().editOriginal("Missing argument '"+SUBCMD_SET+"' or '"+SUBCMD_REMOVE+"' or '"+SUBCMD_VIEW+"'").queue();
			return;
		} else {
			final Long id = event.getGuild().getIdLong();

			if (method.equals(SUBCMD_REMOVE)) {
				configService.setRequiredTitleRegex(id, null);
				event.getHook().editOriginal("Removed title regex").queue();
			} else if (method.equals(SUBCMD_SET)) {
				final String regex = event.getOption(OPTION_REGEX).getAsString().strip();
				configService.setRequiredTitleRegex(id, regex);
				event.getHook().editOriginal("Set title regex to: "+regex).queue();
			} else if (method.equals(SUBCMD_VIEW)) {
				final String regex = configService.getRequiredTitleRegex(id);
				if (regex == null || regex.isEmpty()) {
					event.getHook().editOriginal("There is no regex set.").queue();
				} else {
					event.getHook().editOriginal("The title regex is: "+regex).queue();
				}
			} else {
				event.getHook().editOriginal("Incorrect argument. Needs '"+SUBCMD_SET+"' or '"+SUBCMD_REMOVE+"' or '"+SUBCMD_VIEW+"'").queue();
			}
		}
	}
	
	private void txtChannel(SlashCommandEvent event) {
		final String method = event.getSubcommandName();
		if (method == null) {
			event.getHook().editOriginal("Missing argument '"+SUBCMD_SET+"' or '"+SUBCMD_REMOVE+"' or '"+SUBCMD_VIEW+"'").queue();
			return;
		} else {
			final Long id = event.getGuild().getIdLong();
			
			if (method.equals(SUBCMD_REMOVE)) {
				configService.setOutputChannel(id, null);
				event.getHook().editOriginal("Removed output channel").queue();
			} else if (method.equals(SUBCMD_SET)) {
				final ChannelType chantype = event.getOption(OPTION_CHANNEL).getChannelType();
				if (!chantype.equals(ChannelType.TEXT)) {
					event.getHook().editOriginal("You must specify a Text Channel. Your channel was of type '"+chantype.toString()+"'").queue();
					return;
				}
				final MessageChannel channel = event.getOption(OPTION_CHANNEL).getAsMessageChannel();
				configService.setOutputChannel(id, channel.getIdLong());
				event.getHook().editOriginal("Set output channel to: "+channel.getName()).queue();
			} else if (method.equals(SUBCMD_VIEW)) {
				final Long channelId = configService.getOutputChannel(id);
				final MessageChannel channel = event.getGuild().getTextChannelById(channelId != null ? channelId : 0L);
				if (channel != null) {
					event.getHook().editOriginal("The livestream output channel is set to: "+channel.getName()).queue();
				} else {
					event.getHook().editOriginal("There is no livestream output channel set.").queue();
				}
			} else {
				event.getHook().editOriginal("Incorrect argument. Needs '"+SUBCMD_SET+"' or '"+SUBCMD_REMOVE+"' or '"+SUBCMD_VIEW+"'").queue();
			}
		}
	}
	
	private void mentionRole(SlashCommandEvent event) {
		final String method = event.getSubcommandName();
		if (method == null) {
			event.getHook().editOriginal("Missing argument '"+SUBCMD_SET+"' or '"+SUBCMD_REMOVE+"' or '"+SUBCMD_VIEW+"'").queue();
			return;
		} else {
			final Long id = event.getGuild().getIdLong();
			
			if (method.equals(SUBCMD_REMOVE)) {
				configService.setMentionRole(id, null);
				event.getHook().editOriginal("Removed mention role").queue();
			} else if (method.equals(SUBCMD_SET)) {
				final Role role = event.getOption(OPTION_ROLE).getAsRole();
				configService.setMentionRole(id, role.getIdLong());	
				event.getHook().editOriginal("Set mention role to: "+role.getName()+". This will be mentioned every time any streamer goes live.").queue();
			} else if (method.equals(SUBCMD_VIEW)) {
				final Long roleId = configService.getMentionRole(id);
				final Role role = event.getGuild().getRoleById(roleId != null ? roleId : 0L);
				if (role != null) {
					event.getHook().editOriginal("The mention role is set to: "+role.getName()).queue();
				} else {
					event.getHook().editOriginal("There is no mention role set.").queue();
				}
			} else {
				event.getHook().editOriginal("Incorrect argument. Needs '"+SUBCMD_SET+"' or '"+SUBCMD_REMOVE+"' or '"+SUBCMD_VIEW+"'").queue();
			}
		}
	}
	
	private void categoryWatch(SlashCommandEvent event) {
		final String method = event.getSubcommandName();
		if (method == null) {
			event.getHook().editOriginal("Missing argument '"+SUBCMD_ADD+"' or '"+SUBCMD_REMOVE+"' or '"+SUBCMD_VIEW+"' or '"+SUBCMD_DELALL+"'").queue();
			return;
		} else {
			final Long id = event.getGuild().getIdLong();
			
			if (method.equals(SUBCMD_DELALL)) {
				Long count = categoryService.delAll(id);
				event.getHook().editOriginal("Removed all categories ("+count+") from the watch list.").queue();
			} else if (method.equals(SUBCMD_REMOVE)) {
				final String category = event.getOption(OPTION_CATEGORY).getAsString();
				boolean success = categoryService.remove(id, category);
				if (success) {
					event.getHook().editOriginal("Removed category '"+category+"' from the watch list.").queue();
				} else {
					event.getHook().editOriginal("Category '"+category+"' was already not in watch list. Nothing removed.").queue();
				}
			} else if (method.equals(SUBCMD_VIEW)) {
				List<DefinedCategory> categories = categoryService.getAll(id);
				if (categories == null || categories.size() == 0) {
					event.getHook().editOriginal("There are no categories in the watch list.").queue();
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append(categories.size() + " categories: ```\n");
					for (DefinedCategory c : categories) {
						sb.append(c.getId().getCategory() + ", ");
					}
					String result = sb.toString();
					result = result.substring(0, Math.min(result.length() - 2, 2000));
					event.getHook().editOriginal(result).queue();
				}
			} else if (method.equals(SUBCMD_ADD)) {
				final String category = event.getOption(OPTION_CATEGORY).getAsString();
				boolean success = categoryService.add(id, category);
				if (success) {
					event.getHook().editOriginal("Added category '"+category+"' to the watch list.").queue();
				} else {
					event.getHook().editOriginal("Category '"+category+"' was already in the watch list. Nothing added.").queue();
				}
			}
		}
	}
	
	private void streamerWatch(SlashCommandEvent event) {
		final String method = event.getSubcommandName();
		if (method == null) {
			event.getHook().editOriginal("Missing argument '"+SUBCMD_ADD+"' or '"+SUBCMD_REMOVE+"' or '"+SUBCMD_VIEW+"' or '"+SUBCMD_DELALL+"'").queue();
			return;
		} else {
			final Long id = event.getGuild().getIdLong();
			
			if (method.equals(SUBCMD_DELALL)) {
				Long count = channelService.delAll(id);
				event.getHook().editOriginal("Removed all streamers ("+count+") from the watch list.").queue();
			} else if (method.equals(SUBCMD_REMOVE)) {
				final String channel = event.getOption(OPTION_STREAMER).getAsString();
				boolean success = channelService.remove(id, channel);
				if (success) {
					event.getHook().editOriginal("Removed streamer '"+channel+"' from the watch list.").queue();
				} else {
					event.getHook().editOriginal("Streamer '"+channel+"' was already not in watch list. Nothing removed.").queue();
				}
			} else if (method.equals(SUBCMD_VIEW)) {
				List<DefinedChannel> channels = channelService.getAll(id);
				if (channels == null || channels.size() == 0) {
					event.getHook().editOriginal("There are no streamers in the watch list.").queue();
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append(channels.size() + " channels: ```\n");
					for (DefinedChannel c : channels) {
						sb.append(c.getId().getChannel() + ", ");
					}
					String result = sb.toString();
					result = result.substring(0, Math.min(result.length() - 2, 2000));
					event.getHook().editOriginal(result).queue();
				}
			} else if (method.equals(SUBCMD_ADD)) {
				final String channel = event.getOption(OPTION_STREAMER).getAsString();
				boolean success = channelService.add(id, channel);
				if (success) {
					event.getHook().editOriginal("Added streamer '"+channel+"' to the watch list.").queue();
				} else {
					event.getHook().editOriginal("Streamer '"+channel+"' was already in the watch list. Nothing added.").queue();
				}
			}
		}
	}
	
	private void wlStreamer(SlashCommandEvent event) {
		final String method = event.getSubcommandName();
		if (method == null) {
			event.getHook().editOriginal("Missing argument '"+SUBCMD_ADD+"' or '"+SUBCMD_REMOVE+"' or '"+SUBCMD_VIEW+"' or '"+SUBCMD_DELALL+"'").queue();
			return;
		} else {
			final Long id = event.getGuild().getIdLong();
			
			if (method.equals(SUBCMD_DELALL)) {
				Long count = wlChannelService.delAll(id);
				event.getHook().editOriginal("Removed all streamers ("+count+") from the whitelist.").queue();
			} else if (method.equals(SUBCMD_REMOVE)) {
				final String channel = event.getOption(OPTION_STREAMER).getAsString();
				boolean success = wlChannelService.remove(id, channel);
				if (success) {
					event.getHook().editOriginal("Removed streamer '"+channel+"' from the whitelist.").queue();
				} else {
					event.getHook().editOriginal("Streamer '"+channel+"' was already not in whitelist. Nothing removed.").queue();
				}
			} else if (method.equals(SUBCMD_VIEW)) {
				List<WhitelistedChannel> channels = wlChannelService.getAll(id);
				if (channels == null || channels.size() == 0) {
					event.getHook().editOriginal("There are no streamers in the whitelist.").queue();
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append(channels.size() + " channels: ```\n");
					for (WhitelistedChannel c : channels) {
						sb.append(c.getId().getChannel() + ", ");
					}
					String result = sb.toString();
					result = result.substring(0, Math.min(result.length() - 2, 2000));
					event.getHook().editOriginal(result).queue();
				}
			} else if (method.equals(SUBCMD_ADD)) {
				final String channel = event.getOption(OPTION_STREAMER).getAsString();
				boolean success = wlChannelService.add(id, channel);
				if (success) {
					event.getHook().editOriginal("Added streamer '"+channel+"' to the whitelist.").queue();
				} else {
					event.getHook().editOriginal("Streamer '"+channel+"' was already in the whitelist. Nothing added.").queue();
				}
			}
		}
	}
	
	private void wlCategory(SlashCommandEvent event) {
		final String method = event.getSubcommandName();
		if (method == null) {
			event.getHook().editOriginal("Missing argument '"+SUBCMD_ADD+"' or '"+SUBCMD_REMOVE+"' or '"+SUBCMD_VIEW+"' or '"+SUBCMD_DELALL+"'").queue();
			return;
		} else {
			final Long id = event.getGuild().getIdLong();
			
			if (method.equals(SUBCMD_DELALL)) {
				Long count = wlCategoryService.delAll(id);
				event.getHook().editOriginal("Removed all categories ("+count+") from the whitelist.").queue();
			} else if (method.equals(SUBCMD_REMOVE)) {
				final String channel = event.getOption(OPTION_STREAMER).getAsString();
				boolean success = wlCategoryService.remove(id, channel);
				if (success) {
					event.getHook().editOriginal("Removed category '"+channel+"' from the whitelist.").queue();
				} else {
					event.getHook().editOriginal("Category '"+channel+"' was already not in whitelist. Nothing removed.").queue();
				}
			} else if (method.equals(SUBCMD_VIEW)) {
				List<WhitelistedCategory> channels = wlCategoryService.getAll(id);
				if (channels == null || channels.size() == 0) {
					event.getHook().editOriginal("There are no categories in the whitelist.").queue();
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append(channels.size() + " channels: ```\n");
					for (WhitelistedCategory c : channels) {
						sb.append(c.getId().getCategory() + ", ");
					}
					String result = sb.toString();
					result = result.substring(0, Math.min(result.length() - 2, 2000));
					event.getHook().editOriginal(result).queue();
				}
			} else if (method.equals(SUBCMD_ADD)) {
				final String channel = event.getOption(OPTION_STREAMER).getAsString();
				boolean success = wlCategoryService.add(id, channel);
				if (success) {
					event.getHook().editOriginal("Added category '"+channel+"' to the whitelist.").queue();
				} else {
					event.getHook().editOriginal("Category '"+channel+"' was already in the whitelist. Nothing added.").queue();
				}
			}
		}
	}
	
	private void blStreamer(SlashCommandEvent event) {
		final String method = event.getSubcommandName();
		if (method == null) {
			event.getHook().editOriginal("Missing argument '"+SUBCMD_ADD+"' or '"+SUBCMD_REMOVE+"' or '"+SUBCMD_VIEW+"' or '"+SUBCMD_DELALL+"'").queue();
			return;
		} else {
			final Long id = event.getGuild().getIdLong();
			
			if (method.equals(SUBCMD_DELALL)) {
				Long count = blChannelService.delAll(id);
				event.getHook().editOriginal("Removed all streamers ("+count+") from the blacklist.").queue();
			} else if (method.equals(SUBCMD_REMOVE)) {
				final String channel = event.getOption(OPTION_STREAMER).getAsString();
				boolean success = blChannelService.remove(id, channel);
				if (success) {
					event.getHook().editOriginal("Removed streamer '"+channel+"' from the blacklist.").queue();
				} else {
					event.getHook().editOriginal("Streamer '"+channel+"' was already not in blacklist. Nothing removed.").queue();
				}
			} else if (method.equals(SUBCMD_VIEW)) {
				List<BlacklistedChannel> channels = blChannelService.getAll(id);
				if (channels == null || channels.size() == 0) {
					event.getHook().editOriginal("There are no streamers in the blacklist.").queue();
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append(channels.size() + " channels: ```\n");
					for (BlacklistedChannel c : channels) {
						sb.append(c.getId().getChannel() + ", ");
					}
					String result = sb.toString();
					result = result.substring(0, Math.min(result.length() - 2, 2000));
					event.getHook().editOriginal(result).queue();
				}
			} else if (method.equals(SUBCMD_ADD)) {
				final String channel = event.getOption(OPTION_STREAMER).getAsString();
				boolean success = blChannelService.add(id, channel);
				if (success) {
					event.getHook().editOriginal("Added streamer '"+channel+"' to the blacklist.").queue();
				} else {
					event.getHook().editOriginal("Streamer '"+channel+"' was already in the blacklist. Nothing added.").queue();
				}
			}
		}
	}
	
	private void blCategory(SlashCommandEvent event) {
		final String method = event.getSubcommandName();
		if (method == null) {
			event.getHook().editOriginal("Missing argument '"+SUBCMD_ADD+"' or '"+SUBCMD_REMOVE+"' or '"+SUBCMD_VIEW+"' or '"+SUBCMD_DELALL+"'").queue();
			return;
		} else {
			final Long id = event.getGuild().getIdLong();
			
			if (method.equals(SUBCMD_DELALL)) {
				Long count = blCategoryService.delAll(id);
				event.getHook().editOriginal("Removed all categories ("+count+") from the blacklist.").queue();
			} else if (method.equals(SUBCMD_REMOVE)) {
				final String channel = event.getOption(OPTION_STREAMER).getAsString();
				boolean success = blCategoryService.remove(id, channel);
				if (success) {
					event.getHook().editOriginal("Removed category '"+channel+"' from the blacklist.").queue();
				} else {
					event.getHook().editOriginal("Category '"+channel+"' was already not in blacklist. Nothing removed.").queue();
				}
			} else if (method.equals(SUBCMD_VIEW)) {
				List<BlacklistedCategory> channels = blCategoryService.getAll(id);
				if (channels == null || channels.size() == 0) {
					event.getHook().editOriginal("There are no categories in the blacklist.").queue();
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append(channels.size() + " channels: ```\n");
					for (BlacklistedCategory c : channels) {
						sb.append(c.getId().getCategory() + ", ");
					}
					String result = sb.toString();
					result = result.substring(0, Math.min(result.length() - 2, 2000));
					event.getHook().editOriginal(result).queue();
				}
			} else if (method.equals(SUBCMD_ADD)) {
				final String channel = event.getOption(OPTION_STREAMER).getAsString();
				boolean success = blCategoryService.add(id, channel);
				if (success) {
					event.getHook().editOriginal("Added category '"+channel+"' to the blacklist.").queue();
				} else {
					event.getHook().editOriginal("Category '"+channel+"' was already in the blacklist. Nothing added.").queue();
				}
			}
		}
	}

}
