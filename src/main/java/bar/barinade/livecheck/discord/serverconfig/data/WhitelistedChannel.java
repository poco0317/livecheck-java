package bar.barinade.livecheck.discord.serverconfig.data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import bar.barinade.livecheck.discord.serverconfig.data.pk.WhitelistedChannelId;

/**
 * If WhitelistedChannel is present, any streamer resolved through categories must be listed here.
 *
 */
@Entity
@Table(name = "whitelisted_channels")
public class WhitelistedChannel {
	
	@EmbeddedId
	private WhitelistedChannelId id;

	public WhitelistedChannelId getId() {
		return id;
	}

	public void setId(WhitelistedChannelId id) {
		this.id = id;
	}
	
}
