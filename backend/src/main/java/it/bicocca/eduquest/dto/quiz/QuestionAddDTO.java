package it.bicocca.eduquest.dto.quiz;

import it.bicocca.eduquest.domain.quiz.Difficulty;

public class QuestionAddDTO {
	private String text;
	private String topic;
	
	private Difficulty difficulty;
	private QuestionType questionType;

	public QuestionAddDTO() {

	}

	public QuestionAddDTO(String text, Difficulty difficulty, String topic, QuestionType questionType) {
		this.text = text;
		this.difficulty = difficulty;
		this.topic = topic;
		this.questionType = questionType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}
	
}
