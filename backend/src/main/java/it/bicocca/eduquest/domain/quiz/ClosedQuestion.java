package it.bicocca.eduquest.domain.quiz;

import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.dto.quiz.QuestionType;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "closed_questions")
@PrimaryKeyJoinColumn(name = "question_id")
public class ClosedQuestion extends Question {

	@OneToMany(mappedBy = "closedQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClosedQuestionOption> options = new ArrayList<>();
	
	public ClosedQuestion(String text, String topic, User author, Difficulty difficulty) {
		super(text, topic, author, QuestionType.CLOSED, difficulty);
	}
	
	public void addOption(ClosedQuestionOption option) {
        options.add(option);
        option.setClosedQuestion(this);
    }
	
	public List<ClosedQuestionOption> getOptions() { 
		return options; 
	}
	
}
