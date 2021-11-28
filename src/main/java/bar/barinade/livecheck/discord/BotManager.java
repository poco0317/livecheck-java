package bar.barinade.livecheck.discord;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import bar.barinade.livecheck.discord.handler.CommandHandler;
import bar.barinade.livecheck.discord.handler.CommandHandlerBase;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Service
public class BotManager {
	
	private static final Logger m_logger = LoggerFactory.getLogger(BotManager.class);
	
	@Value("${discord.token}")
	private String token;
	
	private JDA jdaBot;
	
	@PostConstruct
	public void initialize() throws LoginException, InterruptedException {
		m_logger.info("Initializing BotManager");
		JDABuilder builder = JDABuilder.createDefault(token);
		
		// bunch of stuff we dont need
		builder.disableCache(
				CacheFlag.ACTIVITY,
				CacheFlag.CLIENT_STATUS,
				CacheFlag.EMOTE,
				CacheFlag.MEMBER_OVERRIDES,
				CacheFlag.ONLINE_STATUS,
				CacheFlag.ROLE_TAGS,
				CacheFlag.VOICE_STATE
				);
		
		// even more stuff we dont need
		builder.disableIntents(
				GatewayIntent.DIRECT_MESSAGE_REACTIONS,
				GatewayIntent.DIRECT_MESSAGE_TYPING,
				GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.GUILD_BANS,
				GatewayIntent.GUILD_EMOJIS,
				GatewayIntent.GUILD_INVITES,
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_MESSAGE_REACTIONS,
				GatewayIntent.GUILD_MESSAGE_TYPING,
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_PRESENCES,
				GatewayIntent.GUILD_VOICE_STATES,
				GatewayIntent.GUILD_WEBHOOKS
				);
		
		// stuff that might matter
		builder.setChunkingFilter(ChunkingFilter.NONE);
		builder.setMemberCachePolicy(MemberCachePolicy.NONE);
		builder.setBulkDeleteSplittingEnabled(false);
		builder.setCompression(Compression.ZLIB);
		builder.setAutoReconnect(true);
		
		// fun
		builder.setActivity(Activity.playing("Gaming"));
		
		// how to care about commands
		final CommandHandlerBase cmd1 = new CommandHandler();
		builder.addEventListeners(cmd1);
		
		// about to finish making the client...
		m_logger.info("Waiting for login");
		JDA result = builder.build();
		jdaBot = result;
		jdaBot.awaitReady();
		m_logger.info("Login finished");

		m_logger.info("Registering commands");
		// how to care about slash commands
		for (final CommandData cmd : cmd1.getCommandsToUpsert()) {
			result.upsertCommand(cmd).queue();
		}
		
		m_logger.info("BotManager initialize finished");
	}

}
