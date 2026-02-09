package it.bicocca.eduquest.domain.multimedia;

import jakarta.persistence.*;

@Entity
@Table(name = "multimedia_supports")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "content_type", discriminatorType = DiscriminatorType.STRING)
public abstract class MultimediaSupport {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String url; // Cloudinary or YouTube

    private String caption;

	public long getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String getCaption() {
		return caption;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public abstract MultimediaType getType();

}

