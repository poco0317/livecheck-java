package bar.barinade.livecheck.discord.serverconfig.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bar.barinade.livecheck.discord.serverconfig.data.DefinedCategory;
import bar.barinade.livecheck.discord.serverconfig.data.DefinedCategoryId;

@Repository
public interface DefinedCategoryRepo extends JpaRepository<DefinedCategory, DefinedCategoryId> {

	List<DefinedCategory> findByDefinedCategoryIdGuildId(Long id);
	Long deleteByDefinedCategoryIdGuildId(Long id);
}
