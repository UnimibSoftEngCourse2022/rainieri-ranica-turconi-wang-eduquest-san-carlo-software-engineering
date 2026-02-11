package it.bicocca.eduquest.domain.multimedia;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("VIDEO")
public class VideoSupport extends MultimediaSupport {
	@Column(name = "is_youtube")
	private Boolean isYoutube;

	public VideoSupport() {
		// Default constructor
	}

	public Boolean getIsYoutube() {
		return isYoutube;
	}

	public void setIsYoutube(Boolean isYoutube) {
		this.isYoutube = isYoutube;
	}

	@Override
	public MultimediaType getType() {
		return MultimediaType.VIDEO;
	}
	
	
	
}
