package it.bicocca.eduquest.domain.quiz;

import java.util.ArrayList;
import java.util.List;

import it.bicocca.eduquest.domain.users.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "closed_questions")
@PrimaryKeyJoinColumn(name = "question_id")
public class ClosedQuestion extends Question {

	@OneToMany(mappedBy = "closedQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClosedQuestionOption> options = new ArrayList<>();
	
	public ClosedQuestion() {
		
	}

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
