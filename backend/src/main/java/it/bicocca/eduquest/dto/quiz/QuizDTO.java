package it.bicocca.eduquest.dto.quiz;
 
import java.util.List;

import it.bicocca.eduquest.domain.quiz.Difficulty;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QuizDTO {
	@JsonProperty("id")	         	 private final long id;
	@JsonProperty("title")       	 private final String title;
	@JsonProperty("description") 	 private final String description;
	@JsonProperty("teacherAuthorId") private final long teacherAuthorId;
	private final Difficulty difficulty;
	private final long maxScore;
	private final List<QuestionDTO> questions;
	
	public QuizDTO(long id, String title, String description, long teacherAuthorId, long maxScore, List<QuestionDTO> questions) {
		this.id = id;
		this.title = title;
		this.description = description;
		// FIXME max score and difficulty should be calculated from questions, not arrive from DTO
		this.difficulty = Difficulty.UNDEFINED;
		this.maxScore = maxScore;
		this.questions = questions;
		this.teacherAuthorId = teacherAuthorId;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
	
	public Difficulty getDifficulty() {
		return difficulty;
	}

	public long getMaxScore() {
		return maxScore;
	}

	public List<QuestionDTO> getQuestions() {
		return questions;
	}
	
	public long getTeacherAuthorId() {
		return teacherAuthorId;
	}
}
