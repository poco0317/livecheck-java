package bar.barinade.livecheck.streams.data;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import bar.barinade.livecheck.discord.serverconfig.data.ServerConfiguration;

@Embeddable
public class PostedLivestreamId implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guild_id", nullable = false)
	private ServerConfiguration guild;
	
	@Column(name = "message_id", nullable = false)
	private Long messageId;
	
	@OneToOne
	@JoinColumns({
		@JoinColumn(name = "platform", nullable = false),
		@JoinColumn(name = "name", nullable = false)
	})
	private LivestreamInfo info;
	
	public PostedLivestreamId() {}

	public PostedLivestreamId(ServerConfiguration guild, Long messageId, LivestreamInfo info) {
		this.guild = guild;
		this.messageId = messageId;
		this.info = info;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public LivestreamInfo getInfo() {
		return info;
	}

	public void setInfo(LivestreamInfo info) {
		this.info = info;
	}
	
	public ServerConfiguration getGuild() {
		return guild;
	}

	public void setGuild(ServerConfiguration guild) {
		this.guild = guild;
	}

	@Override
	public int hashCode() {
		return Objects.hash(guild, info, messageId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostedLivestreamId other = (PostedLivestreamId) obj;
		return Objects.equals(guild, other.guild) && Objects.equals(info, other.info)
				&& Objects.equals(messageId, other.messageId);
	}
	
}
