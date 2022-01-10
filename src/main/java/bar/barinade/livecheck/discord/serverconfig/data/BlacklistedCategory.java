package bar.barinade.livecheck.discord.serverconfig.data;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * If a streamer happens to be in this category, their stream will not show up.
 *
 */
@Entity
@Table(name = "blacklisted_categories")
public class BlacklistedCategory {
	
	@Id
	@Column(name = "category", nullable = false)
	private String category;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guild_id", nullable = false)
	private ServerConfiguration guild;

}
