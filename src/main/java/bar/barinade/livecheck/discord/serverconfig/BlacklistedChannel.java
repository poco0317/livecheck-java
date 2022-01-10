package bar.barinade.livecheck.discord.serverconfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * If this streamer shows up, using category detection, they will be removed from the list.
 *
 */
@Entity
@Table(name = "blacklisted_channels")
public class BlacklistedChannel {

	@Id
	@Column(name = "channel", nullable = false)
	private String channel;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guild_id", nullable = false)
	private ServerConfiguration guild;
	
}
