package it.bicocca.eduquest.dto.quiz;
 
import java.util.List;

import it.bicocca.eduquest.domain.quiz.Difficulty;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QuizAddDTO {
	private final String title;
	private final String description;
	private final long teacherAuthorId;
	
	public QuizAddDTO(String title, String description, long teacherAuthorId) {
		this.title = title;
		this.description = description;
		this.teacherAuthorId = teacherAuthorId;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
	
	public long getTeacherAuthorId() {
		return teacherAuthorId;
	}
}
