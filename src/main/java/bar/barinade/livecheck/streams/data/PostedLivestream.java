package bar.barinade.livecheck.streams.data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import bar.barinade.livecheck.streams.data.pk.PostedLivestreamId;

@Entity
@Table(name = "posted_livestreams")
public class PostedLivestream {
	
	@EmbeddedId
	private PostedLivestreamId id;
	
	@Column(name = "channel_id", nullable = false)
	private Long channelId;

	public PostedLivestreamId getId() {
		return id;
	}

	public void setId(PostedLivestreamId id) {
		this.id = id;
	}

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

}
