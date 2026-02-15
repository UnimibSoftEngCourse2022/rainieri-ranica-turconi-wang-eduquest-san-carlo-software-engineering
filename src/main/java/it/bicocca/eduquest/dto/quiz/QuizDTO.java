package it.bicocca.eduquest.dto.quiz;

import java.util.List;
import it.bicocca.eduquest.domain.quiz.Difficulty;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QuizDTO {
	private long id;
	private final String title;
	private final String description;
	private final long teacherAuthorId;
	private final List<QuestionDTO> questions;
	private final QuizStatsDTO quizStats;
	private final Difficulty difficulty;
	@JsonProperty("isPublic")
	private boolean isPublic;
	
	public QuizDTO() {
		this.title = "";
		this.description = "";
		this.teacherAuthorId = 0;
		this.questions = null;
		this.quizStats = null;
		this.difficulty = null;
    }

	public QuizDTO(long id, String title, String description, long teacherAuthorId, List<QuestionDTO> questions, QuizStatsDTO quizStats, Difficulty difficulty, boolean isPublic) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.teacherAuthorId = teacherAuthorId;
		this.questions = questions;
		this.quizStats = quizStats;
		this.difficulty = difficulty;
		this.isPublic = isPublic;
	}

	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
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
	
	public List<QuestionDTO> getQuestions() {
		return questions;
	}
	
	public QuizStatsDTO getQuizStats() {
		return quizStats;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	
}
