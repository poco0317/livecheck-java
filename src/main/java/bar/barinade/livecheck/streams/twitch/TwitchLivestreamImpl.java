package bar.barinade.livecheck.streams.twitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.TwitchHelixBuilder;
import com.github.twitch4j.helix.domain.Game;
import com.github.twitch4j.helix.domain.GameList;
import com.github.twitch4j.helix.domain.InboundFollowers;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.UserList;

import bar.barinade.livecheck.streams.LivestreamImpl;
import bar.barinade.livecheck.streams.data.LivestreamInfo;
import bar.barinade.livecheck.streams.data.pk.LivestreamInfoId;

@Component
public class TwitchLivestreamImpl extends LivestreamImpl {

	private static final Logger m_logger = LoggerFactory.getLogger(TwitchLivestreamImpl.class);
	
	// IMPORTANT:
	// If a valid clientID + clientSecret is given, any Token required by the Helix API can be set null.
	
	@Value("${streams.twitch.clientid}")
	private String clientId;
	@Value("${streams.twitch.clientsecret}")
	private String clientSecret;
	
	private TwitchHelix api;
	
	// cache for Twitch Games/Categories
	// (prevent extra api calls)
	private ConcurrentHashMap<String, Game> nameToGameMap = new ConcurrentHashMap<>();
	
	@PostConstruct
	private void initialize() {
		m_logger.info("Initializing Twitch Livestream Implementation");
		
		api = TwitchHelixBuilder.builder()
				.withClientId(clientId)
				.withClientSecret(clientSecret)
				.withUserAgent("BarinadeBot/0.0.1")
				.build();
				
		m_logger.info("Finished initializing Twitch Livestream Implementation");
	}

	@Override
	public Platform getPlatform() {
		return Platform.TWITCH;
	}
	
	@Override
	public String getStreamUrl(LivestreamInfo info) {
		return "https://twitch.tv/"+info.getId().getName().toLowerCase();
	}

	@Override
	public List<LivestreamInfo> getLivestreams(List<String> categoryNames, List<String> channelNames) {
		m_logger.info("Getting Twitch livestreams - {} categories | {} channels", categoryNames.size(), channelNames.size());
		
		// to guarantee uniqueness (but if letter case starts differing ... dont care because the site also cares about that so its just extra garbage)
		HashSet<String> categoryNameSet = new HashSet<>(categoryNames);
		categoryNames.clear();
		categoryNames.addAll(categoryNameSet);
		HashSet<Game> uniqueGameList = getGamesFromCategories(categoryNames);
		List<String> gameIds = new ArrayList<>(uniqueGameList.size());
		uniqueGameList.forEach(game -> gameIds.add(game.getId()));
		
		// to guarantee uniqueness
		HashSet<String> channelNamesSet = new HashSet<>(channelNames);
		channelNames.clear();
		channelNames.addAll(channelNamesSet);
		
		// who is actually live?
		HashSet<Stream> liveStreams = new HashSet<>();
		
		// let's find out
		liveStreams.addAll(chunkedPaginatingStreamGetter(gameIds, false));
		liveStreams.addAll(chunkedPaginatingStreamGetter(channelNames, true));
		m_logger.info("Found {} Twitch livestreams", liveStreams.size());
		
		List<LivestreamInfo> result = new ArrayList<>(liveStreams.size());
		for (Stream stream : liveStreams) {
			// rather than thrashing the db just make a bunch of new things
			// there is no way that could go wrong
			// (we can take all these copies and the result is, basically each rerun resets the entire table of LivestreamInfos)
			LivestreamInfo info = new LivestreamInfo();
			LivestreamInfoId id = new LivestreamInfoId();
			id.setName(stream.getUserLogin());
			id.setPlatform(getPlatform());
			info.setId(id);
			info.setCurrentViewers(stream.getViewerCount().longValue());
			info.setCategory(stream.getGameName() != null ? stream.getGameName() : "Unknown");
			info.setThumbnailUrl(stream.getThumbnailUrl(256, 144));
			info.setDescription(null);
			info.setFollowers(null);
			info.setStatus(null);
			info.setTotalViews(null);
			info.setTitle(stream.getTitle());
			info.setAvatarUrl(null);
			result.add(info);
		}
		fillMiscInfo(result);
		
		return result;
	}
	
	/**
	 * Get Games using a given name and cache each result. This also stores the ID for use.
	 */
	private HashSet<Game> getGamesFromCategories(List<String> categoryNames) {
		m_logger.debug("Getting games from {} categories", categoryNames.size());
		HashSet<Game> output = new HashSet<>();
		
		Iterator<String> it = categoryNames.iterator();
		while (it.hasNext()) {
			final String name = it.next();
			final Game game = nameToGameMap.getOrDefault(name, null);
			if (game != null) {
				output.add(game);
				it.remove();
			}
		}
		
		if (categoryNames.size() > 0) {
			// need to resolve remaining game ids
			int startIndex = 0;
			int endIndex = Math.min(100, categoryNames.size());
			List<String> idChunk = categoryNames.subList(startIndex, endIndex);
			while (idChunk.size() > 0 && startIndex < categoryNames.size()) {
				GameList result = api.getGames(null, null, idChunk, null).execute();
				output.addAll(result.getGames());
				
				// add to cache
				for (Game game : result.getGames()) {
					nameToGameMap.put(game.getName(), game);
				}
				startIndex = endIndex;
				endIndex = Math.min(categoryNames.size(), endIndex + 100);
				idChunk = categoryNames.subList(startIndex, endIndex);
			}
		}
		
		m_logger.debug("Got {} games from {} categories", output.size(), categoryNames.size());
		return output;
	}
	
	/**
	 * The API acts in a way that does not match the documentation!
	 * If given both a category and a user, the results are of a search using the query as a filter. That is, you probably will get 0 results.
	 * Must search for one query type at a time to get the desired effect.
	 * Search once for users and once again for games (chunking by 100s and paginating by 100s of results...)
	 */
	private HashSet<Stream> chunkedPaginatingStreamGetter(List<String> queryList, boolean forUsers) {
		m_logger.trace("Beginning chunked paginating stream getter | {} entries | {} forUsers", queryList.size(), forUsers);
		HashSet<Stream> output = new HashSet<>();
		
		// chunking into 100s
		int upperBound = Math.min(100, queryList.size());
		int lowerBound = 0;
		List<String> chunk = queryList.size() > 0 ? queryList.subList(lowerBound, upperBound) : null;
				
		// repeat until exhausted all chunks
		while ((chunk != null && chunk.size() > 0) ||
				(lowerBound != upperBound)) {
			
			StreamList sl = apiGetStreams(chunk, forUsers, null);
			output.addAll(sl.getStreams());
			
			// a result of more than 100 - we have to paginate...
			String cursor = sl.getPagination().getCursor();
			while (cursor != null) {
				sl = apiGetStreams(chunk, forUsers, cursor);
				output.addAll(sl.getStreams());
				cursor = sl.getPagination().getCursor();
			}
			
			if (lowerBound == upperBound) {
				chunk = null;
			} else {
				lowerBound = upperBound;
				upperBound = Math.min(queryList.size(), upperBound + 100);
				chunk = queryList.subList(lowerBound, upperBound);
			}
		}
		
		m_logger.trace("Returning {} streams", output.size());
		return output;
	}
	
	private StreamList apiGetStreams(List<String> chunk, boolean forUsers, String cursor) {
		if (forUsers) {
			// chunks are User Logins (usernames)
			return api.getStreams(null, cursor, null, 100, null, null, null, chunk).execute();
		} else {
			// chunks are Game IDs
			return api.getStreams(null, cursor, null, 100, chunk, null, null, null).execute();
		}
	}
	
	private List<LivestreamInfo> fillMiscInfo(List<LivestreamInfo> streams) {
		
		List<String> names = new ArrayList<>(streams.size());
		streams.forEach(stream -> names.add(stream.getId().getName()));
		
		class MiscData {
			public MiscData(String description, Integer total, String status, Integer views, String profileImageUrl) {
				this.description = description;
				this.followers = total;
				this.status = status;
				this.totalViews = views;
				this.profileImageUrl = profileImageUrl;
			}
			public String description;
			public Integer followers;
			public String status;
			public Integer totalViews;
			public String profileImageUrl;
		}
		
		HashMap<String, MiscData> datamap = new HashMap<>();
		
		// chunking into 100s
		int upperBound = Math.min(100, names.size());
		int lowerBound = 0;
		List<String> chunk = names.size() > 0 ? names.subList(lowerBound, upperBound) : null;
				
		// repeat until exhausted all chunks
		while ((chunk != null && chunk.size() > 0) ||
				(lowerBound != upperBound)) {
			
			UserList ul = api.getUsers(null, null, chunk).execute();
			ul.getUsers().forEach(user -> {
				String id = user.getId();
				InboundFollowers follows = api.getChannelFollowers(null, id, null, 1, null).execute();
				//FollowList follows = api.getFollowers(null, null, id, null, 1).execute();
				datamap.put(
						user.getLogin(),
						new MiscData(
							user.getDescription(),
							follows.getTotal(),
							user.getBroadcasterType(),
							0,
							user.getProfileImageUrl()
							)
						);
			});
			
			if (lowerBound == upperBound) {
				chunk = null;
			} else {
				lowerBound = upperBound;
				upperBound = Math.min(names.size(), upperBound + 100);
				chunk = names.subList(lowerBound, upperBound);
			}
		}
		
		// fill the data
		streams.forEach(stream -> {
			MiscData data = datamap.get(stream.getId().getName());
			stream.setDescription(data.description);
			stream.setFollowers(data.followers.longValue());
			stream.setTotalViews(data.totalViews.longValue());
			stream.setStatus(data.status);
			stream.setAvatarUrl(data.profileImageUrl);
		});
		
		return streams;
	}
	
}
