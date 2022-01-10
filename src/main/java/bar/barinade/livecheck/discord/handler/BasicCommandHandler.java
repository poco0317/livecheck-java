package bar.barinade.livecheck.discord.handler;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

@Component
@Scope("prototype")
public class BasicCommandHandler extends CommandHandlerBase {
	
	@Override
	public CommandData[] getCommandsToUpsert() {
		return new CommandData[] {
			new CommandData("test", "test slash command")
		};
	}
	
	void cmd_test(SlashCommandEvent event) {
		event.reply("test worked")
		.setEphemeral(true)
		.queue();
	}

}
