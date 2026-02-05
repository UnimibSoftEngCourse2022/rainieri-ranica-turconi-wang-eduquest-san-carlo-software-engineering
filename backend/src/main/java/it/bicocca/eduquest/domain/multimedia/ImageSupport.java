package it.bicocca.eduquest.domain.multimedia;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("IMAGE")
public class ImageSupport extends MultimediaSupport {

	public ImageSupport() {
		this.setType(MultimediaType.IMAGE);
	}
	
}
