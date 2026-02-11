package it.bicocca.eduquest.dto.quiz;

import java.util.List;
import it.bicocca.eduquest.domain.multimedia.*;
import it.bicocca.eduquest.domain.quiz.Difficulty;

public class QuestionAddDTO {
	private String text;
	private String topic;
	
	private Difficulty difficulty;
	private QuestionType questionType;
	
	private List<String> validAnswersOpenQuestion;
	private List<ClosedQuestionOptionDTO> closedQuestionOptions;
	
	private MultimediaType multimediaType;
    private String multimediaUrl;

	public QuestionAddDTO() {
		
	}

	public QuestionAddDTO(String text, String topic, Difficulty difficulty, QuestionType questionType,
			List<String> validAnswersOpenQuestion, List<ClosedQuestionOptionDTO> closedQuestionOptions) {
		this.text = text;
		this.topic = topic;
		this.difficulty = difficulty;
		this.questionType = questionType;
		this.validAnswersOpenQuestion = validAnswersOpenQuestion;
		this.closedQuestionOptions = closedQuestionOptions;
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

	public List<String> getValidAnswersOpenQuestion() {
		return validAnswersOpenQuestion;
	}

	public void setValidAnswersOpenQuestion(List<String> validAnswersOpenQuestion) {
		this.validAnswersOpenQuestion = validAnswersOpenQuestion;
	}

	public List<ClosedQuestionOptionDTO> getClosedQuestionOptions() {
		return closedQuestionOptions;
	}

	public void setClosedQuestionOptions(List<ClosedQuestionOptionDTO> closedQuestionOptions) {
		this.closedQuestionOptions = closedQuestionOptions;
	}

	public MultimediaType getMultimediaType() {
		return multimediaType;
	}

	public String getMultimediaUrl() {
		return multimediaUrl;
	}

	public void setMultimediaType(MultimediaType multimediaType) {
		this.multimediaType = multimediaType;
	}

	public void setMultimediaUrl(String multimediaUrl) {
		this.multimediaUrl = multimediaUrl;
	}
	
}
