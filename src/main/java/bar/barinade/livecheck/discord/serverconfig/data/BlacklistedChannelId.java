package bar.barinade.livecheck.discord.serverconfig.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class BlacklistedChannelId implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "channel", nullable = false)
	private String channel;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guild_id", nullable = false)
	private ServerConfiguration guild;

	public BlacklistedChannelId() {}
	
	public BlacklistedChannelId(String channel, ServerConfiguration guild) {
		this.channel = channel;
		this.guild = guild;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public ServerConfiguration getGuild() {
		return guild;
	}

	public void setGuild(ServerConfiguration guild) {
		this.guild = guild;
	}
}
