package it.bicocca.eduquest.domain.quiz;

import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.dto.quiz.QuestionType;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "open_questions")
@PrimaryKeyJoinColumn(name = "question_id")
public class OpenQuestion extends Question {
	
	@OneToMany(mappedBy = "openQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpenQuestionAcceptedAnswer> validAnswers = new ArrayList<>();
	
	public OpenQuestion() {
		
	}

	public OpenQuestion(String text, String topic, User author, Difficulty difficulty) {
		super(text, topic, author, QuestionType.OPENED, difficulty);
	}
	
	public void addAnswer(OpenQuestionAcceptedAnswer answer) {
        validAnswers.add(answer);
        answer.setOpenQuestion(this); 
    }

    public List<OpenQuestionAcceptedAnswer> getValidAnswers() { 
    	return validAnswers; 
    }
	
}
