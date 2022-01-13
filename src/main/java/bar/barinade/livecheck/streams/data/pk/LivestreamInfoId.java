package bar.barinade.livecheck.streams.data.pk;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import bar.barinade.livecheck.streams.LivestreamImpl;
import bar.barinade.livecheck.streams.LivestreamImpl.Platform;

@Embeddable
public class LivestreamInfoId implements Serializable {
	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	@Column(name = "platform", nullable = false)
	private LivestreamImpl.Platform platform;
	
	@Column(name = "name", nullable = false)
	private String name;

	public LivestreamInfoId() {}
	
	public LivestreamInfoId(Platform platform, String name) {
		this.platform = platform;
		this.name = name;
	}

	@Override
	public String toString() {
		return "LivestreamInfoId [platform=" + platform + ", name=" + name + "]";
	}

	public LivestreamImpl.Platform getPlatform() {
		return platform;
	}

	public void setPlatform(LivestreamImpl.Platform platform) {
		this.platform = platform;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, platform);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LivestreamInfoId other = (LivestreamInfoId) obj;
		return Objects.equals(name, other.name) && platform == other.platform;
	}
	
}
