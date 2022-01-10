package bar.barinade.livecheck.streams.data.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import bar.barinade.livecheck.streams.data.PostedLivestream;

public interface PostedLivestreamRepo extends JpaRepository<PostedLivestream, UUID> {

}
