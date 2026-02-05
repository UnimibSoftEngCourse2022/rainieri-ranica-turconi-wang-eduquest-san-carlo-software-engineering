package it.bicocca.eduquest.domain.multimedia;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("VIDEO")
public class VideoSupport extends MultimediaSupport {
	public boolean isYoutube;

	public VideoSupport() {
		this.setType(MultimediaType.VIDEO);
	}

	public boolean isYoutube() {
		return isYoutube;
	}

	public void setYoutube(boolean isYoutube) {
		this.isYoutube = isYoutube;
	}
	
}
