package bar.barinade.livecheck.discord.serverconfig.data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import bar.barinade.livecheck.discord.serverconfig.data.pk.BlacklistedChannelId;


/**
 * If this streamer shows up, using category detection, they will be removed from the list.
 *
 */
@Entity
@Table(name = "blacklisted_channels")
public class BlacklistedChannel {

	@EmbeddedId
	private BlacklistedChannelId id;

	public BlacklistedChannelId getId() {
		return id;
	}

	public void setId(BlacklistedChannelId id) {
		this.id = id;
	}
	
}
