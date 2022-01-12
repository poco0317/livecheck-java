package bar.barinade.livecheck.streams.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bar.barinade.livecheck.streams.data.LivestreamInfo;
import bar.barinade.livecheck.streams.data.pk.LivestreamInfoId;

@Repository
public interface LivestreamInfoRepo extends JpaRepository<LivestreamInfo, LivestreamInfoId> {

}
