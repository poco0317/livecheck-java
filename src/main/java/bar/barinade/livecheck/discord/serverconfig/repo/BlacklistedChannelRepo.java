package bar.barinade.livecheck.discord.serverconfig.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bar.barinade.livecheck.discord.serverconfig.data.BlacklistedChannel;
import bar.barinade.livecheck.discord.serverconfig.data.BlacklistedChannelId;

@Repository
public interface BlacklistedChannelRepo extends JpaRepository<BlacklistedChannel, BlacklistedChannelId> {

	List<BlacklistedChannel> findByIdGuildId(Long id);
	Long deleteByIdGuildId(Long id);
}
