package bar.barinade.livecheck.streams.data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import bar.barinade.livecheck.streams.data.pk.PostedLivestreamId;

@Entity
@Table(name = "posted_livestreams")
public class PostedLivestream {
	
	@EmbeddedId
	private PostedLivestreamId id;

	public PostedLivestreamId getId() {
		return id;
	}

	public void setId(PostedLivestreamId id) {
		this.id = id;
	}

}
