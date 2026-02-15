package it.bicocca.eduquest.dto.quizAttempt;

import it.bicocca.eduquest.domain.quiz.QuestionType;

public class AnswerDTO {
	private Long id;
	
	private Long questionId;
	private QuestionType questionType;
	
	private String textOpenAnswer;
	
	private Long selectedOptionId;
	private String selectedOptionText;
	
	private Long quizAttemptId;
	
	private Boolean isCorrect;

	public AnswerDTO() {
		
	}

	@SuppressWarnings("java:S107")
	public AnswerDTO(Long id, Long quizAttemptId, Long questionId, QuestionType type, String textOpenAnswer, Long selectedOptionId, String selectedOptionText, Boolean isCorrect) {
        this.id = id;
        this.quizAttemptId = quizAttemptId;
        this.questionId = questionId;
        this.questionType = type;
        this.textOpenAnswer = textOpenAnswer;
        this.selectedOptionId = selectedOptionId;
        this.selectedOptionText = selectedOptionText;
        this.isCorrect = isCorrect;
	}

	public Long getId() {
		return id;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public String getTextOpenAnswer() {
		return textOpenAnswer;
	}

	public Long getSelectedOptionId() {
		return selectedOptionId;
	}

	public String getSelectedOptionText() {
		return selectedOptionText;
	}

	public Long getQuizAttemptId() {
		return quizAttemptId;
	}

	public Boolean isCorrect() {
		return isCorrect;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}

	public void setTextOpenAnswer(String textOpenAnswer) {
		this.textOpenAnswer = textOpenAnswer;
	}

	public void setSelectedOptionId(Long selectedOptionId) {
		this.selectedOptionId = selectedOptionId;
	}

	public void setSelectedOptionText(String selectedOptionText) {
		this.selectedOptionText = selectedOptionText;
	}

	public void setQuizAttemptId(Long quizAttemptId) {
		this.quizAttemptId = quizAttemptId;
	}

	public void setCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}	
	
}
