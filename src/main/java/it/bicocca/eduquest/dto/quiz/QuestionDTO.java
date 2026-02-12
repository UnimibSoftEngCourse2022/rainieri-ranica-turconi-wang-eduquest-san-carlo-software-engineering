package it.bicocca.eduquest.dto.quiz;

import java.util.List;
import it.bicocca.eduquest.dto.multimedia.*;
import it.bicocca.eduquest.domain.quiz.Difficulty;
import it.bicocca.eduquest.domain.quiz.QuestionType;

public class QuestionDTO {
	private final long id;
	private final String text;
	private final Difficulty difficulty;
	private final String topic;
	
	private final QuestionType questionType;
	
	private final List<String> validAnswersOpenQuestion;
	private final List<ClosedQuestionOptionDTO> closedQuestionOptions;
	
	private final long authorId;
	
	private QuestionStatsDTO stats;
	
	private MultimediaDTO multimedia;
	
	@SuppressWarnings("java:S107")
	public QuestionDTO(long id, String text, Difficulty difficulty, String topic, QuestionType questionType, List<String> validAnswersOpenQuestion, List<ClosedQuestionOptionDTO> closedQuestionOptions, long authorId, QuestionStatsDTO stats) {
		this.id = id;
		this.text = text;
		this.difficulty = difficulty;
		this.topic = topic;
		this.questionType = questionType;
		this.validAnswersOpenQuestion = validAnswersOpenQuestion;
		this.closedQuestionOptions = closedQuestionOptions;
		this.authorId = authorId;
		this.stats = stats;
	}

	public QuestionStatsDTO getStats() {
		return stats;
	}

	public long getAuthorId() {
		return authorId;
	}

	public long getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public String getTopic() {
		return topic;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public List<String> getValidAnswersOpenQuestion() {
		return validAnswersOpenQuestion;
	}

	public List<ClosedQuestionOptionDTO> getClosedQuestionOptions() {
		return closedQuestionOptions;
	}

	public MultimediaDTO getMultimedia() {
		return multimedia;
	}

	public void setMultimedia(MultimediaDTO multimedia) {
		this.multimedia = multimedia;
	}
	
	public void setQuestionStats(QuestionStatsDTO questionStats) {
		this.stats = questionStats;
	}
	
}
