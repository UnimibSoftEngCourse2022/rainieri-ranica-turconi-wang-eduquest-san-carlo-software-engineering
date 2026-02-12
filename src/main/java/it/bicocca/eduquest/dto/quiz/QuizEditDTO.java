package it.bicocca.eduquest.dto.quiz;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuizEditDTO {
	private String title;
	private String description;
	@JsonProperty("isPublic")
	private boolean isPublic;
	
	public QuizEditDTO() {

	}

	public QuizEditDTO(String title, String description, boolean isPublic) {
		this.title = title;
		this.description = description;
		this.isPublic = isPublic;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	
}
