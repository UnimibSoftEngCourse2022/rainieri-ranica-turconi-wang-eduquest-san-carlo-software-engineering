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

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", insertable = false, updatable = false)
    private MultimediaType type; 

    private String caption;

	public long getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public MultimediaType getType() {
		return type;
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

	public void setType(MultimediaType type) {
		this.type = type;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

}

