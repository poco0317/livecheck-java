package bar.barinade.livecheck.discord.serverconfig.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import bar.barinade.livecheck.discord.serverconfig.data.DefinedCategory;

public interface DefinedCategoryRepo extends JpaRepository<DefinedCategory, String> {

}
