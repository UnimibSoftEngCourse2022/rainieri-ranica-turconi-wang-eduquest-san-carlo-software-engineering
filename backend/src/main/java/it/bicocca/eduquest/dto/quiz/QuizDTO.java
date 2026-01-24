package it.bicocca.eduquest.dto.quiz;
 
import java.time.Duration; 
import java.util.List;

public class QuizDTO {
	private final long id;
	private final String title;
	private final String description;
	private final Duration duration;
	private final int maxTries;
	private final List<QuestionDTO> questions;
	
	public QuizDTO(long id, String title, String description, Duration duration, int maxTries, List<QuestionDTO> questions) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.duration = duration;
		this.maxTries = maxTries;
		this.questions = questions;
	}
}
