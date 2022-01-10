package bar.barinade.livecheck.discord.serverconfig.data;


import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "server_configs")
public class ServerConfiguration {
	
	@Id
	@Column(name = "guild_id")
	private Long id;
	
	// the ID of the channel to post streams to
	@Column(name = "channel_id", nullable = true)
	private Long channelId;
	
	// if present, each stream title must match this regular expression
	@Column(name = "title_regex", nullable = true)
	private String titleRegex;
	
	// if present, this role is mentioned every time a streamer goes live
	@Column(name = "mention_role_id", nullable = true)
	private Long mentionRoleId;
	
	@OneToMany(mappedBy = "guild")
	private Set<BlacklistedCategory> blacklistedCategories;
	
	@OneToMany(mappedBy = "guild")
	private Set<BlacklistedChannel> blacklistedChannels;
	
	@OneToMany(mappedBy = "guild")
	private Set<WhitelistedCategory> whitelistedCategories;
	
	@OneToMany(mappedBy = "guild")
	private Set<WhitelistedChannel> whitelistedChannels;
	
	@OneToMany(mappedBy = "guild")
	private Set<DefinedCategory> definedCategories;
	
	@OneToMany(mappedBy = "guild")
	private Set<DefinedChannel> definedChannels;

}
