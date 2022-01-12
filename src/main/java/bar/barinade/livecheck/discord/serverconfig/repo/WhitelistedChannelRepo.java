package bar.barinade.livecheck.discord.serverconfig.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bar.barinade.livecheck.discord.serverconfig.data.WhitelistedChannel;
import bar.barinade.livecheck.discord.serverconfig.data.WhitelistedChannelId;

@Repository
public interface WhitelistedChannelRepo extends JpaRepository<WhitelistedChannel, WhitelistedChannelId> {

	List<WhitelistedChannel> findByIdGuildId(Long id);
	Long deleteByIdGuildId(Long id);
}
