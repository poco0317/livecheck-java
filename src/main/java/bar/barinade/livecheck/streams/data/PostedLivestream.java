package bar.barinade.livecheck.streams.data;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import bar.barinade.livecheck.discord.serverconfig.data.ServerConfiguration;

@Entity
@Table(name = "posted_livestreams")
public class PostedLivestream {
	
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	private UUID id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guild_id", nullable = false)
	private ServerConfiguration guildId;
	
	@Column(name = "message_id", nullable = false)
	private Long messageId;
	
	@OneToOne
	@JoinColumn(name = "stream_info_id", nullable = false)
	private LivestreamInfo info;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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
	
	public ServerConfiguration getGuildId() {
		return guildId;
	}

	public void setGuildId(ServerConfiguration guildId) {
		this.guildId = guildId;
	}

}
