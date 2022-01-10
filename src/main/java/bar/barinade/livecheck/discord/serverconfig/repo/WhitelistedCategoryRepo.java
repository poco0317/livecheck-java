package bar.barinade.livecheck.discord.serverconfig.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import bar.barinade.livecheck.discord.serverconfig.WhitelistedCategory;

public interface WhitelistedCategoryRepo extends JpaRepository<WhitelistedCategory, String> {

}
