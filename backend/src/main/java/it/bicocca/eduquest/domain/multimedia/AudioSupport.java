package it.bicocca.eduquest.domain.multimedia;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("AUDIO")
public class AudioSupport extends MultimediaSupport {

	public AudioSupport() {
		// Default constructor
	}

	@Override
	public MultimediaType getType() {
		return MultimediaType.AUDIO;
	}
	
}
