package it.bicocca.eduquest.dto.quiz;

import it.bicocca.eduquest.domain.quiz.Difficulty;
import java.util.List;

public class QuestionDTO {
	private final long id;
	private final String text;
	private final Difficulty difficulty;
	private final String topic;
	
	private final QuestionType questionType;
	
	private final List<String> validAnswersOpenQuestion;
	private final List<ClosedQuestionOptionDTO> closedQuestionOptions;
	
	public QuestionDTO(long id, String text, Difficulty difficulty, String topic, QuestionType questionType, List<String> validAnswersOpenQuestion, List<ClosedQuestionOptionDTO> closedQuestionOptions) {
		this.id = id;
		this.text = text;
		this.difficulty = difficulty;
		this.topic = topic;
		this.questionType = questionType;
		this.validAnswersOpenQuestion = validAnswersOpenQuestion;
		this.closedQuestionOptions = closedQuestionOptions;
	}
}
