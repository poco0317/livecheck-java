package bar.barinade.livecheck.streams.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
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
	@JoinColumn(name = "stream_info_id", nullable = false)
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
	
}
