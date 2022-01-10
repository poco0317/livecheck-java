package bar.barinade.livecheck.discord.serverconfig.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import bar.barinade.livecheck.discord.serverconfig.data.BlacklistedChannel;

public interface BlacklistedChannelRepo extends JpaRepository<BlacklistedChannel, String> {

}
