package bar.barinade.livecheck.discord.serverconfig.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bar.barinade.livecheck.discord.serverconfig.data.WhitelistedChannel;

@Repository
public interface WhitelistedChannelRepo extends JpaRepository<WhitelistedChannel, String> {

}
