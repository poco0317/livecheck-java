package bar.barinade.livecheck.discord.serverconfig.data;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import bar.barinade.livecheck.discord.serverconfig.data.pk.BlacklistedCategoryId;


/**
 * If a streamer happens to be in this category, their stream will not show up.
 *
 */
@Entity
@Table(name = "blacklisted_categories")
public class BlacklistedCategory {
	
	@EmbeddedId
	private BlacklistedCategoryId id;

	public BlacklistedCategoryId getId() {
		return id;
	}

	public void setId(BlacklistedCategoryId id) {
		this.id = id;
	}

}
