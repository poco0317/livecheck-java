package bar.barinade.livecheck.streams.data;


import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import bar.barinade.livecheck.streams.data.pk.LivestreamInfoId;

@Entity
@Table(name = "livestream_info")
public class LivestreamInfo {
	
	@EmbeddedId
	private LivestreamInfoId id;
	
	@Column(name = "followers", nullable = false)
	private Long followers;
	
	@Column(name = "views", nullable = false)
	private Long totalViews;
	
	@Column(name = "viewers", nullable = false)
	private Long currentViewers;
	
	@Column(name = "status", nullable = false)
	private String status;
	
	@Column(name = "description", nullable = false)
	private String description;
	
	@OneToMany(mappedBy = "id.info")
	private Set<PostedLivestream> posts;

	public LivestreamInfoId getId() {
		return id;
	}

	public void setId(LivestreamInfoId id) {
		this.id = id;
	}

	public Long getFollowers() {
		return followers;
	}

	public void setFollowers(Long followers) {
		this.followers = followers;
	}

	public Long getTotalViews() {
		return totalViews;
	}

	public void setTotalViews(Long totalViews) {
		this.totalViews = totalViews;
	}

	public Long getCurrentViewers() {
		return currentViewers;
	}

	public void setCurrentViewers(Long currentViewers) {
		this.currentViewers = currentViewers;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<PostedLivestream> getPostedLivestreams() {
		return posts;
	}

	public void setPostedLivestreams(Set<PostedLivestream> postedLivestreams) {
		this.posts = postedLivestreams;
	}

}
