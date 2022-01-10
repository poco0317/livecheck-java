package bar.barinade.livecheck.discord.serverconfig.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bar.barinade.livecheck.discord.serverconfig.data.BlacklistedCategory;

@Repository
public interface BlacklistedCategoryRepo extends JpaRepository<BlacklistedCategory, String> {

}
