package bar.barinade.livecheck.discord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BotManager {
	
	@Value("${discord.token}")
	private String token;
	
	@PostConstruct
	public void initialize() {
		
	}

}
