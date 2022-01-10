package bar.barinade.livecheck.discord.serverconfig.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bar.barinade.livecheck.discord.serverconfig.data.BlacklistedChannel;

@Repository
public interface BlacklistedChannelRepo extends JpaRepository<BlacklistedChannel, String> {

}
