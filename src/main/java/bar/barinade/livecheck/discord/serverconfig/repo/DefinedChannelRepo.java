package bar.barinade.livecheck.discord.serverconfig.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import bar.barinade.livecheck.discord.serverconfig.DefinedChannel;

public interface DefinedChannelRepo extends JpaRepository<DefinedChannel, String> {

}
