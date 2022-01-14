package bar.barinade.livecheck.streams;

import java.util.List;

import bar.barinade.livecheck.streams.data.LivestreamInfo;

public abstract class LivestreamImpl {
		
	/**
	 * This enum is used as the Platform for LivestreamInfo which means existing platforms should not be renamed.
	 * If any are renamed, their data becomes invalidated.
	 */
	public enum Platform {
		TWITCH ("Twitch");
		
		private String stylizedName;
		
		Platform(String stylizedName) {
			this.stylizedName = stylizedName;
		}
		public String stylizedName() {
			return stylizedName;
		}
	}
	
	public abstract Platform getPlatform();
	
	public abstract String getStreamUrl(LivestreamInfo info);
	
	public abstract List<LivestreamInfo> getLivestreams(List<String> categoryNames, List<String> channelNames);

}
