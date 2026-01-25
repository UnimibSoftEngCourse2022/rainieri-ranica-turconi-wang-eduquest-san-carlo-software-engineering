package it.bicocca.eduquest.dto.quiz;
 
import java.util.List;

import it.bicocca.eduquest.domain.quiz.Difficulty;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QuizAddDTO {
	private String title;
	private String description;
	private long teacherAuthorId;
	
	/*public QuizAddDTO(String title, String description, long teacherAuthorId) {
		this.title = title;
		this.description = description;
		this.teacherAuthorId = teacherAuthorId;
	}*/

	public QuizAddDTO() {
		
	}
	
	public QuizAddDTO(String title, String description, long teacherAuthorId) {
		this.title = title;
		this.description = description;
		this.teacherAuthorId = teacherAuthorId;
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

	public long getTeacherAuthorId() {
		return teacherAuthorId;
	}

	public void setTeacherAuthorId(long teacherAuthorId) {
		this.teacherAuthorId = teacherAuthorId;
	}
	
}
