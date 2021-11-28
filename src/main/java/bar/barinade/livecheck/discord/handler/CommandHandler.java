package bar.barinade.livecheck.discord.handler;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CommandHandler extends CommandHandlerBase {
	
	

	@Override
	public void onSlashCommand(SlashCommandEvent event)
	{
		if (!event.getName().equals("test")) return;
		event.reply("test worked")
			.setEphemeral(true)
			.queue();
	}

	@Override
	public CommandData[] getCommandsToUpsert() {
		return new CommandData[] {
			new CommandData("test", "test slash command")
		};
	}

}
