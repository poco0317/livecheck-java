package bar.barinade.livecheck.discord.serverconfig.data.pk;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import bar.barinade.livecheck.discord.serverconfig.data.ServerConfiguration;

@Embeddable
public class DefinedCategoryId implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "category", nullable = false)
	private String category;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guild_id", nullable = false)
	private ServerConfiguration guild;

	public DefinedCategoryId() {}
	
	public DefinedCategoryId(String category, ServerConfiguration guild) {
		this.category = category;
		this.guild = guild;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public ServerConfiguration getGuild() {
		return guild;
	}

	public void setGuild(ServerConfiguration guild) {
		this.guild = guild;
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, guild);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefinedCategoryId other = (DefinedCategoryId) obj;
		return Objects.equals(category, other.category) && Objects.equals(guild, other.guild);
	}
	
	
}
