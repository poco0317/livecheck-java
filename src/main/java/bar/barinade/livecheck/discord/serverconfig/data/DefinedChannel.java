package bar.barinade.livecheck.discord.serverconfig.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The defined list of streamers or channels to watch from any platform.
 *
 */
@Entity
@Table(name = "defined_channels")
public class DefinedChannel {

	@Id
	@Column(name = "channel", nullable = false)
	private String channel;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guild_id", nullable = false)
	private ServerConfiguration guild;
}
