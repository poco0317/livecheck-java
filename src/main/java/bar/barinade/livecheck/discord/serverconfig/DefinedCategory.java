package bar.barinade.livecheck.discord.serverconfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The list of entire categories to pull from to watch. Categories may also be Games
 *
 */
@Entity
@Table(name = "defined_categories")
public class DefinedCategory {

	@Id
	@Column(name = "category", nullable = false)
	private String category;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guild_id", nullable = false)
	private ServerConfiguration guild;
	
}
