package bar.barinade.livecheck.discord.serverconfig.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bar.barinade.livecheck.discord.serverconfig.data.DefinedChannel;

@Repository
public interface DefinedChannelRepo extends JpaRepository<DefinedChannel, String> {

}
