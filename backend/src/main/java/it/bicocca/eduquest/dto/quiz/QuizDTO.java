package it.bicocca.eduquest.dto.quiz;

import java.util.List;

public class QuizDTO {
	private long id;
	private final String title;
	private final String description;
	private final long teacherAuthorId;
	private final List<QuestionDTO> questions;
	private final QuizStatsDTO quizStats;
	
	public QuizDTO(long id, String title, String description, long teacherAuthorId, List<QuestionDTO> questions, QuizStatsDTO quizStats) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.teacherAuthorId = teacherAuthorId;
		this.questions = questions;
		this.quizStats = quizStats;
	}

	public long getId() {
		return this.id;
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
}
