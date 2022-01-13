package bar.barinade.livecheck.streams;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import bar.barinade.livecheck.discord.serverconfig.service.ServerConfigService;
import bar.barinade.livecheck.streams.data.LivestreamInfo;
import bar.barinade.livecheck.streams.data.repo.LivestreamInfoRepo;
import bar.barinade.livecheck.streams.data.repo.PostedLivestreamRepo;
import bar.barinade.livecheck.streams.twitch.TwitchLivestreamImpl;

@Service
public class LivestreamManager {
	
	private static final Logger m_logger = LoggerFactory.getLogger(LivestreamManager.class);
	
	private ConcurrentHashMap<LivestreamImpl.Platform, LivestreamImpl> streamApis = new ConcurrentHashMap<>(); 

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
		streamApis.put(LivestreamImpl.Platform.TWITCH, springContext.getBean(TwitchLivestreamImpl.class));
		
		m_logger.info("LivestreamManager initialize finished");
	}
	
	/**
	 * Collect all streams for all servers and update appropriately.
	 * This function normally should be run on a loop, doing most of the work.
	 */
	public void aggregate() {
		
		// all streamers
		List<LivestreamInfo> allStreamsBefore = streamRepo.findAll();
		
		// all streamers mapped out by platform
		// this mapping is adjusted based on the streamers that went live or offline, to derive the livestreams to post
		HashMap<LivestreamImpl.Platform, HashMap<String, LivestreamInfo>> streamersByPlatform = new HashMap<>();
		for (LivestreamInfo stream : allStreamsBefore) {
			final LivestreamImpl.Platform platform = stream.getId().getPlatform();
			if (!streamersByPlatform.containsKey(platform)) {
				streamersByPlatform.put(platform, new HashMap<>());
			}
			streamersByPlatform.get(platform).put(stream.getId().getName(), stream);
		}
		
		
	}
}
