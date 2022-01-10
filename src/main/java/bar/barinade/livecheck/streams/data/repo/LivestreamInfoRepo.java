package bar.barinade.livecheck.streams.data.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bar.barinade.livecheck.streams.data.LivestreamInfo;

@Repository
public interface LivestreamInfoRepo extends JpaRepository<LivestreamInfo, UUID> {

}
