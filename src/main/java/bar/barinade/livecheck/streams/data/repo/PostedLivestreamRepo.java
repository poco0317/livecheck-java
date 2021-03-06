package bar.barinade.livecheck.streams.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bar.barinade.livecheck.streams.data.PostedLivestream;
import bar.barinade.livecheck.streams.data.pk.PostedLivestreamId;

@Repository
public interface PostedLivestreamRepo extends JpaRepository<PostedLivestream, PostedLivestreamId> {

	List<PostedLivestream> findByIdGuildId(Long id);
	Long deleteByIdGuildId(Long id);
}
