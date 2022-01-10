package bar.barinade.livecheck.discord.serverconfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * If any whitelisted category is present, all streamers resolved must be using any of the whitelisted categories.
 *
 */
@Entity
@Table(name = "whitelisted_categories")
public class WhitelistedCategory {

	@Id
	@Column(name = "category", nullable = false)
	private String category;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guild_id", nullable = false)
	private ServerConfiguration guild;
}
