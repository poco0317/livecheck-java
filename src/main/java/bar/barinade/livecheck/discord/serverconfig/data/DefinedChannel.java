package bar.barinade.livecheck.discord.serverconfig.data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The defined list of streamers or channels to watch from any platform.
 *
 */
@Entity
@Table(name = "defined_channels")
public class DefinedChannel {

	@EmbeddedId
	private DefinedChannelId id;

	public DefinedChannelId getId() {
		return id;
	}

	public void setId(DefinedChannelId id) {
		this.id = id;
	}
	
}
