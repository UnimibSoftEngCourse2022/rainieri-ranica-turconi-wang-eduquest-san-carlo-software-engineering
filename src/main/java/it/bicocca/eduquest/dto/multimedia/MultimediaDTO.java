package it.bicocca.eduquest.dto.multimedia;

import it.bicocca.eduquest.domain.multimedia.MultimediaType;

public class MultimediaDTO {
	private String url;
    private MultimediaType type;
    private boolean isYoutube;

    public MultimediaDTO() {}

    public MultimediaDTO(String url, MultimediaType type, boolean isYoutube) {
        this.url = url;
        this.type = type;
        this.isYoutube = isYoutube;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public MultimediaType getType() { return type; }
    public void setType(MultimediaType type) { this.type = type; }

    public boolean getIsYoutube() { return isYoutube; }
    public void setIsYoutube(boolean isYoutube) { this.isYoutube = isYoutube; }
}
