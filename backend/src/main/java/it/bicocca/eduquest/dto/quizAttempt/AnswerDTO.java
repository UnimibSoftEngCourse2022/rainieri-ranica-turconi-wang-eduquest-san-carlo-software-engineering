package it.bicocca.eduquest.dto.quizAttempt;

import it.bicocca.eduquest.dto.quiz.QuestionType;

public class AnswerDTO {
	private long id;
	
	private long questionId;
	private QuestionType questionType;
	
	private String textOpenAnswer;
	
	private long selectedOptionId;
	private String selectedOptionText;
	
	private long quizAttemptId;
	
	private boolean isCorrect;

	public AnswerDTO() {
		
	}

	public AnswerDTO(long id, long quizAttemptId, long questionId, QuestionType type, String textOpenAnswer, long selectedOptionId, String selectedOptionText, Boolean isCorrect) {
        this.id = id;
        this.quizAttemptId = quizAttemptId;
        this.questionId = questionId;
        this.questionType = type;
        this.textOpenAnswer = textOpenAnswer;
        this.selectedOptionId = selectedOptionId;
        this.selectedOptionText = selectedOptionText;
        this.isCorrect = isCorrect;
	}

	public long getId() {
		return id;
	}

	public long getQuestionId() {
		return questionId;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public String getTextOpenAnswer() {
		return textOpenAnswer;
	}

	public long getSelectedOptionId() {
		return selectedOptionId;
	}

	public String getSelectedOptionText() {
		return selectedOptionText;
	}

	public long getQuizAttemptId() {
		return quizAttemptId;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}

	public void setTextOpenAnswer(String textOpenAnswer) {
		this.textOpenAnswer = textOpenAnswer;
	}

	public void setSelectedOptionId(long selectedOptionId) {
		this.selectedOptionId = selectedOptionId;
	}

	public void setSelectedOptionText(String selectedOptionText) {
		this.selectedOptionText = selectedOptionText;
	}

	public void setQuizAttemptId(long quizAttemptId) {
		this.quizAttemptId = quizAttemptId;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}	
	
}
