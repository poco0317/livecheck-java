package bar.barinade.livecheck.discord.serverconfig.data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * If any whitelisted category is present, all streamers resolved must be using any of the whitelisted categories.
 *
 */
@Entity
@Table(name = "whitelisted_categories")
public class WhitelistedCategory {

	@EmbeddedId
	private WhitelistedCategoryId id;

	public WhitelistedCategoryId getId() {
		return id;
	}

	public void setId(WhitelistedCategoryId id) {
		this.id = id;
	}
	
}
