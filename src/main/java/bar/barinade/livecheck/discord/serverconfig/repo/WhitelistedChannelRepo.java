package bar.barinade.livecheck.discord.serverconfig.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import bar.barinade.livecheck.discord.serverconfig.WhitelistedChannel;

public interface WhitelistedChannelRepo extends JpaRepository<WhitelistedChannel, String> {

}
