package bar.barinade.livecheck.discord.serverconfig.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import bar.barinade.livecheck.discord.serverconfig.ServerConfiguration;

public interface ServerConfigurationRepo extends JpaRepository<ServerConfiguration, Long> {

}
