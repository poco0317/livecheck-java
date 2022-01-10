package bar.barinade.livecheck.discord.serverconfig.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * If WhitelistedChannel is present, any streamer resolved through categories must be listed here.
 *
 */
@Entity
@Table(name = "whitelisted_channels")
public class WhitelistedChannel {
	
	@Id
	@Column(name = "channel", nullable = false)
	private String channel;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guild_id", nullable = false)
	private ServerConfiguration guild;

}
