package bar.barinade.livecheck.discord.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import bar.barinade.livecheck.streams.LivestreamManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
@Scope("prototype")
public class BasicMessageHandler extends ListenerAdapter {
	
	private static final String CMD_REFRESH = "lc!refresh";
	private static final String CMD_CLEAN = "lc!clean";

	@Autowired
	private LivestreamManager streamManager;
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		
		if (event.getAuthor().getIdLong() == 104461925009608704L) {

			String msg = event.getMessage().getContentDisplay();
			
			if (CMD_REFRESH.equals(msg)) {
				streamManager.aggregate();
				event.getChannel().sendMessage("Globally refreshed all streams.").queue();
			} else if (CMD_CLEAN.equals(msg)) {
				streamManager.cleanup();
				event.getChannel().sendMessage("Globally removed all messages sent by this bot.").queue();
			}
		}
	}
}
