package bar.barinade.livecheck.streams;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import bar.barinade.livecheck.discord.serverconfig.ServerConfigService;
import bar.barinade.livecheck.streams.data.repo.LivestreamInfoRepo;
import bar.barinade.livecheck.streams.data.repo.PostedLivestreamRepo;
import bar.barinade.livecheck.streams.twitch.TwitchLivestreamImpl;

@Service
public class LivestreamManager {
	
	private static final Logger m_logger = LoggerFactory.getLogger(LivestreamManager.class);
	
	private ConcurrentHashMap<LivestreamImpl.Type, LivestreamImpl> streamApis = new ConcurrentHashMap<>(); 

	@Autowired
	private ApplicationContext springContext;
	
	@Autowired
	private ServerConfigService configService;
	
	@Autowired
	private LivestreamInfoRepo streamRepo;
	
	@Autowired
	private PostedLivestreamRepo postRepo;
	
	@PostConstruct
	public void init() {
		m_logger.info("Initializing LivestreamManager");
		
		streamApis.clear();
		streamApis.put(LivestreamImpl.Type.TWITCH, springContext.getBean(TwitchLivestreamImpl.class));
		
		m_logger.info("LivestreamManager initialize finished");
	}
	
	/**
	 * Collect all streams for all servers and update appropriately.
	 * This function normally should be run on a loop, doing most of the work.
	 */
	public void aggregate() {
		
	}
}
