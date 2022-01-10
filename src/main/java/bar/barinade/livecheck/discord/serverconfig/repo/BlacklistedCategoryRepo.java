package bar.barinade.livecheck.discord.serverconfig.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import bar.barinade.livecheck.discord.serverconfig.data.BlacklistedCategory;

public interface BlacklistedCategoryRepo extends JpaRepository<BlacklistedCategory, String> {

}
