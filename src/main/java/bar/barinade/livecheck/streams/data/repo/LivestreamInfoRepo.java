package bar.barinade.livecheck.streams.data.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import bar.barinade.livecheck.streams.data.LivestreamInfo;

public interface LivestreamInfoRepo extends JpaRepository<LivestreamInfo, UUID> {

}
