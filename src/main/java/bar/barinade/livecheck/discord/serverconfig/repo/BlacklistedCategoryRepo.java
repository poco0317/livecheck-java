package bar.barinade.livecheck.discord.serverconfig.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bar.barinade.livecheck.discord.serverconfig.data.BlacklistedCategory;
import bar.barinade.livecheck.discord.serverconfig.data.BlacklistedCategoryId;

@Repository
public interface BlacklistedCategoryRepo extends JpaRepository<BlacklistedCategory, BlacklistedCategoryId> {
	
	List<BlacklistedCategory> findByIdGuildId(Long id);
	Long deleteByIdGuildId(Long id);

}
