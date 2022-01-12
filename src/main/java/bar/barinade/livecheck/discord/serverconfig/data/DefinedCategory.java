package bar.barinade.livecheck.discord.serverconfig.data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import bar.barinade.livecheck.discord.serverconfig.data.pk.DefinedCategoryId;

/**
 * The list of entire categories to pull from to watch. Categories may also be Games
 *
 */
@Entity
@Table(name = "defined_categories")
public class DefinedCategory {

	@EmbeddedId
	private DefinedCategoryId id;

	public DefinedCategoryId getId() {
		return id;
	}

	public void setId(DefinedCategoryId id) {
		this.id = id;
	}
	
}
