package it.bicocca.eduquest.dto.quizAttempt;

import java.util.List;
import it.bicocca.eduquest.dto.quiz.*;

public class QuizSessionDTO {
	private Long attemptId;
	private String quizTitle;
	private String quizDescription;
	private boolean isResumed;
    private List<QuestionDTO> questions; // "Sanitazed" questions
    
    private List<AnswerDTO> existingAnswers; // Already given answers (empty at first)

	public QuizSessionDTO() {
		
	}

	public QuizSessionDTO(Long attemptId, String quizTitle, String quizDescription, boolean isResumed, List<QuestionDTO> questions,
			List<AnswerDTO> existingAnswers) {
		this.attemptId = attemptId;
		this.quizTitle = quizTitle;
		this.quizDescription = quizDescription;
		this.isResumed = isResumed;
		this.questions = questions;
		this.existingAnswers = existingAnswers;
	}

	public Long getAttemptId() {
		return attemptId;
	}

	public String getQuizTitle() {
		return quizTitle;
	}

	public String getQuizDescription() {
		return quizDescription;
	}

	public List<QuestionDTO> getQuestions() {
		return questions;
	}

	public List<AnswerDTO> getExistingAnswers() {
		return existingAnswers;
	}

	public void setAttemptId(Long attemptId) {
		this.attemptId = attemptId;
	}

	public void setQuizTitle(String quizTitle) {
		this.quizTitle = quizTitle;
	}

	public void setQuizDescription(String quizDescription) {
		this.quizDescription = quizDescription;
	}

	public void setQuestions(List<QuestionDTO> questions) {
		this.questions = questions;
	}

	public void setExistingAnswers(List<AnswerDTO> existingAnswers) {
		this.existingAnswers = existingAnswers;
	}

	public boolean isResumed() {
		return isResumed;
	}

	public void setResumed(boolean isResumed) {
		this.isResumed = isResumed;
	}
    
}
