package bar.barinade.livecheck.discord.handler;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public abstract class CommandHandlerBase extends ListenerAdapter {

	/**
	 * Defines the list of slash commands this handler listens for and needs to register.
	 * If this returns null we cant launch
	 */
	public abstract CommandData[] getCommandsToUpsert();
}
