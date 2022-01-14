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
	
	@Column(name = "followers", nullable = true)
	private Long followers;
	
	@Column(name = "views", nullable = true)
	private Long totalViews;
	
	@Column(name = "viewers", nullable = true)
	private Long currentViewers;
	
	@Column(name = "status", nullable = true)
	private String status;
	
	@Column(name = "title", nullable = true)
	private String title;
	
	@Column(name = "description", nullable = true)
	private String description;
	
	@Column(name = "category", nullable = true)
	private String category;
	
	@Column(name = "thumbnail_url", nullable = true)
	private String thumbnailUrl;
	
	@Column(name = "avatar_url", nullable = true)
	private String avatarUrl;
	
	@OneToMany(mappedBy = "id.info")
	private Set<PostedLivestream> posts;

	@Override
	public String toString() {
		return "LivestreamInfo [id=" + id + ", followers=" + followers + ", totalViews=" + totalViews
				+ ", currentViewers=" + currentViewers + ", status=" + status + ", title=" + title + ", description="
				+ description + ", category=" + category + ", thumbnailUrl=" + thumbnailUrl + ", avatarUrl=" + avatarUrl
				+ ", posts=" + posts + "]";
	}

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Set<PostedLivestream> getPosts() {
		return posts;
	}

	public void setPosts(Set<PostedLivestream> posts) {
		this.posts = posts;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

}
