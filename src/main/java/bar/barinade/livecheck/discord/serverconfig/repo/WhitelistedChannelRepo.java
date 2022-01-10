package bar.barinade.livecheck.discord.serverconfig.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import bar.barinade.livecheck.discord.serverconfig.data.WhitelistedChannel;

public interface WhitelistedChannelRepo extends JpaRepository<WhitelistedChannel, String> {

}
