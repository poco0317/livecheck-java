package bar.barinade.livecheck.streams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LivestreamImpl {
	
	private static final Logger m_logger = LoggerFactory.getLogger(LivestreamImpl.class);
	
	/**
	 * This Type is used as the Platform for LivestreamInfo which means existing platforms should not be renamed.
	 * If any are renamed, their data becomes invalidated.
	 */
	public enum Type {
		
		TWITCH;
		
	}
	
	

}
