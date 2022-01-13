package bar.barinade.livecheck.streams.twitch;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.twitch4j.TwitchClientBuilder;

import bar.barinade.livecheck.streams.LivestreamImpl;

@Component
public class TwitchLivestreamImpl extends LivestreamImpl {

	private static final Logger m_logger = LoggerFactory.getLogger(TwitchLivestreamImpl.class);
	
	@PostConstruct
	private void initialize() {
		m_logger.
		
		TwitchClient client = TwitchClientBuilder.builder();
	}
	
}
