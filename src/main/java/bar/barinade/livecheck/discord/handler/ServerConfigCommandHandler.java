package bar.barinade.livecheck.discord.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
	private static final String GROUPCMD_NAME_CATEGORYWHITELIST = "whitelist category";
	private static final String GROUPCMD_NAME_CATEGORYBLACKLIST = "blacklist category";
	private static final String GROUPCMD_NAME_STREAMER = "streamer";
	private static final String GROUPCMD_NAME_STREAMERWHITELIST = "whitelist streamer";
	private static final String GROUPCMD_NAME_STREAMERBLACKLIST = "blacklist streamer";
	private static final String GROUPCMD_NAME_LIVEROLE = "liverole";
	private static final String SUBCMD_ADD = "add";
	private static final String SUBCMD_REMOVE = "remove";
	private static final String SUBCMD_SET = "set";
	private static final String SUBCMD_LIST = "list";
	private static final String SUBCMD_DELALL = "delall";
	
	private static final String EXPLAIN_STREAMER_WHITELIST = " Having this whitelist set means all streamers must be in this list, if detected via category.";
	private static final String EXPLAIN_CATEGORY_WHITELIST = " Having this whitelist set means all categories must be in this list, if detected via streamer.";
	private static final String EXPLAIN_STREAMER_BLACKLIST = " Having this blacklist set means any streamer found in this list will not show up when live.";
	private static final String EXPLAIN_CATEGORY_BLACKLIST = " Having this blacklist set means any streamer with a category category found in this list will not show up when live.";
	
	@Override
	public CommandData[] getCommandsToUpsert() {
		// oh my god
		return new CommandData[] {
				new CommandData(BASE_CMD_NAME, "Modify Livecheck configuration for this server.")
				.addSubcommandGroups(
						new SubcommandGroupData(GROUPCMD_NAME_REQUIREDTITLE, "Modify the required title Regular Expression.")
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove the required title Regular Expression."),
								new SubcommandData(SUBCMD_SET, "Set the required title Regular Expression. If set, all stream titles must match to show up.")
									.addOption(OptionType.STRING, "regex", "Regular Expression to match to stream titles", true)
						),
						new SubcommandGroupData(GROUPCMD_NAME_TXTCHAN, "Modify the output channel for livestreams in this server.")
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Unset the output channel for livestreams."),
								new SubcommandData(SUBCMD_SET, "Set the output channel for livestreams.")
									.addOption(OptionType.CHANNEL, "channel", "Output channel for livestreams", true)
						),
						new SubcommandGroupData(GROUPCMD_NAME_CATEGORY, "Add categories to or remove categories from the watchlist.")
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove a category from the watchlist.")
									.addOption(OptionType.STRING, "category", "Category to remove", true),
								new SubcommandData(SUBCMD_ADD, "Add a category to the watchlist.")
									.addOption(OptionType.STRING, "category", "Category to add", true),
								new SubcommandData(SUBCMD_LIST, "View the list of categories being watched."),
								new SubcommandData(SUBCMD_DELALL, "Remove all categories from the watchlist.")
						),
						new SubcommandGroupData(GROUPCMD_NAME_STREAMER, "Add streamers to or remove streamers from the watchlist.")
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove a streamer from the watchlist.")
									.addOption(OptionType.STRING, "streamer", "Streamer to remove", true),
								new SubcommandData(SUBCMD_ADD, "Add a streamer to the watchlist.")
									.addOption(OptionType.STRING, "streamer", "Streamer to add", true),
								new SubcommandData(SUBCMD_LIST, "View the list of streamers being watched."),
								new SubcommandData(SUBCMD_DELALL, "Remove all streamers from the watchlist.")
						),
						new SubcommandGroupData(GROUPCMD_NAME_STREAMERWHITELIST, "Modify the streamer whitelist." + EXPLAIN_STREAMER_WHITELIST)
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove a streamer from the whitelist.")
									.addOption(OptionType.STRING, "streamer", "Streamer to remove", true),
								new SubcommandData(SUBCMD_ADD, "Add a streamer to the whitelist." + EXPLAIN_STREAMER_WHITELIST)
									.addOption(OptionType.STRING, "streamer", "Streamer to add", true),
								new SubcommandData(SUBCMD_LIST, "View the streamer whitelist."),
								new SubcommandData(SUBCMD_DELALL, "Remove all streamers from the whitelist.")
						),
						new SubcommandGroupData(GROUPCMD_NAME_STREAMERBLACKLIST, "Modify the streamer blacklist." + EXPLAIN_STREAMER_BLACKLIST)
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove a streamer from the blacklist.")
									.addOption(OptionType.STRING, "streamer", "Streamer to remove", true),
								new SubcommandData(SUBCMD_ADD, "Add a streamer to the blacklist." + EXPLAIN_STREAMER_BLACKLIST)
									.addOption(OptionType.STRING, "streamer", "Streamer to add", true),
								new SubcommandData(SUBCMD_LIST, "View the streamer blacklist."),
								new SubcommandData(SUBCMD_DELALL, "Remove all streamers from the blacklist.")
						),
						new SubcommandGroupData(GROUPCMD_NAME_CATEGORYWHITELIST, "Modify the category whitelist." + EXPLAIN_CATEGORY_WHITELIST)
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove a category from the whitelist.")
									.addOption(OptionType.STRING, "category", "Category to add", true),
								new SubcommandData(SUBCMD_ADD, "Add a category to the whitelist." + EXPLAIN_CATEGORY_WHITELIST)
									.addOption(OptionType.STRING, "category", "Category to add", true),
								new SubcommandData(SUBCMD_LIST, "View the category whitelist."),
								new SubcommandData(SUBCMD_DELALL, "Remove all categories from the whitelist.")
						),
						new SubcommandGroupData(GROUPCMD_NAME_CATEGORYBLACKLIST, "Modify the category blacklist." + EXPLAIN_CATEGORY_BLACKLIST)
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove a category from the blacklist.")
									.addOption(OptionType.STRING, "category", "Category to add", true),
								new SubcommandData(SUBCMD_ADD, "Add a category to the blacklist." + EXPLAIN_CATEGORY_BLACKLIST)
									.addOption(OptionType.STRING, "category", "Category to add", true),
								new SubcommandData(SUBCMD_LIST, "View the category blacklist."),
								new SubcommandData(SUBCMD_DELALL, "Remove all categories from the blacklist.")
						),
						new SubcommandGroupData(GROUPCMD_NAME_LIVEROLE, "Modify the role which is mentioned when any streamer goes live.")
						.addSubcommands(
								new SubcommandData(SUBCMD_REMOVE, "Remove the mention that occurs every time a streamer goes live."),
								new SubcommandData(SUBCMD_SET, "Set the role which is mentioned every time a streamer goes live.")
									.addOption(OptionType.ROLE, "role", "Role to mention")
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
				case GROUPCMD_NAME_LIVEROLE:
					liveRole(event);
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
			event.getHook().editOriginal("Missing argument 'set' or 'remove'").queue();
			return;
		} else {
			
		}
	}
	
	private void txtChannel(SlashCommandEvent event) {
		
	}
	
	private void categoryWatch(SlashCommandEvent event) {
		
	}
	
	private void streamerWatch(SlashCommandEvent event) {
		
	}
	
	private void wlStreamer(SlashCommandEvent event) {
		
	}
	
	private void wlCategory(SlashCommandEvent event) {
		
	}
	
	private void blStreamer(SlashCommandEvent event) {
		
	}
	
	private void blCategory(SlashCommandEvent event) {
		
	}
	
	private void liveRole(SlashCommandEvent event) {
		
	}

}
