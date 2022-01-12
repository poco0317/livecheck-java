package bar.barinade.livecheck.discord.serverconfig.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bar.barinade.livecheck.discord.serverconfig.data.WhitelistedCategory;
import bar.barinade.livecheck.discord.serverconfig.data.pk.WhitelistedCategoryId;

@Repository
public interface WhitelistedCategoryRepo extends JpaRepository<WhitelistedCategory, WhitelistedCategoryId> {

	List<WhitelistedCategory> findByIdGuildId(Long id);
	Long deleteByIdGuildId(Long id);
}
