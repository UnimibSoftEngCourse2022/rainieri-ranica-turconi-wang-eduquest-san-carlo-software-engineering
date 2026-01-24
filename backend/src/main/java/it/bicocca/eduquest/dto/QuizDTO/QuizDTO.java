package it.bicocca.eduquest.dto.QuizDTO;
 
import java.time.Duration; 
import java.util.List;

public class QuizDTO {
	private long id;
	private String title;
	private String description;
	private Duration duration;
	private int maxTries;
	private List<QuestionDTO> questions;
}
