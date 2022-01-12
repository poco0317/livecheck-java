package bar.barinade.livecheck.discord.serverconfig.data;


import java.util.Objects;
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
	
	@OneToMany(mappedBy = "id.guild")
	private Set<BlacklistedCategory> blacklistedCategories;
	
	@OneToMany(mappedBy = "id.guild")
	private Set<BlacklistedChannel> blacklistedChannels;
	
	@OneToMany(mappedBy = "id.guild")
	private Set<WhitelistedCategory> whitelistedCategories;
	
	@OneToMany(mappedBy = "id.guild")
	private Set<WhitelistedChannel> whitelistedChannels;
	
	@OneToMany(mappedBy = "id.guild")
	private Set<DefinedCategory> definedCategories;
	
	@OneToMany(mappedBy = "id.guild")
	private Set<DefinedChannel> definedChannels;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public String getTitleRegex() {
		return titleRegex;
	}

	public void setTitleRegex(String titleRegex) {
		this.titleRegex = titleRegex;
	}

	public Long getMentionRoleId() {
		return mentionRoleId;
	}

	public void setMentionRoleId(Long mentionRoleId) {
		this.mentionRoleId = mentionRoleId;
	}

	public Set<BlacklistedCategory> getBlacklistedCategories() {
		return blacklistedCategories;
	}

	public void setBlacklistedCategories(Set<BlacklistedCategory> blacklistedCategories) {
		this.blacklistedCategories = blacklistedCategories;
	}

	public Set<BlacklistedChannel> getBlacklistedChannels() {
		return blacklistedChannels;
	}

	public void setBlacklistedChannels(Set<BlacklistedChannel> blacklistedChannels) {
		this.blacklistedChannels = blacklistedChannels;
	}

	public Set<WhitelistedCategory> getWhitelistedCategories() {
		return whitelistedCategories;
	}

	public void setWhitelistedCategories(Set<WhitelistedCategory> whitelistedCategories) {
		this.whitelistedCategories = whitelistedCategories;
	}

	public Set<WhitelistedChannel> getWhitelistedChannels() {
		return whitelistedChannels;
	}

	public void setWhitelistedChannels(Set<WhitelistedChannel> whitelistedChannels) {
		this.whitelistedChannels = whitelistedChannels;
	}

	public Set<DefinedCategory> getDefinedCategories() {
		return definedCategories;
	}

	public void setDefinedCategories(Set<DefinedCategory> definedCategories) {
		this.definedCategories = definedCategories;
	}

	public Set<DefinedChannel> getDefinedChannels() {
		return definedChannels;
	}

	public void setDefinedChannels(Set<DefinedChannel> definedChannels) {
		this.definedChannels = definedChannels;
	}

	@Override
	public int hashCode() {
		return Objects.hash(blacklistedCategories, blacklistedChannels, channelId, definedCategories, definedChannels,
				id, mentionRoleId, titleRegex, whitelistedCategories, whitelistedChannels);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerConfiguration other = (ServerConfiguration) obj;
		return Objects.equals(blacklistedCategories, other.blacklistedCategories)
				&& Objects.equals(blacklistedChannels, other.blacklistedChannels)
				&& Objects.equals(channelId, other.channelId)
				&& Objects.equals(definedCategories, other.definedCategories)
				&& Objects.equals(definedChannels, other.definedChannels) && Objects.equals(id, other.id)
				&& Objects.equals(mentionRoleId, other.mentionRoleId) && Objects.equals(titleRegex, other.titleRegex)
				&& Objects.equals(whitelistedCategories, other.whitelistedCategories)
				&& Objects.equals(whitelistedChannels, other.whitelistedChannels);
	}

}
