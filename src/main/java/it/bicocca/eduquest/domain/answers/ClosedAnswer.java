package it.bicocca.eduquest.domain.answers;

import it.bicocca.eduquest.domain.quiz.ClosedQuestionOption;
import it.bicocca.eduquest.domain.quiz.Question;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "closed_answers")
public class ClosedAnswer extends Answer {
  
    @ManyToOne
    @JoinColumn(name = "chosen_option_id")
    private ClosedQuestionOption chosenOption;

    public ClosedAnswer() {
    	
    }

    public ClosedAnswer(QuizAttempt attempt, Question question, ClosedQuestionOption chosenOption) {
		super(attempt, question);
		this.chosenOption = chosenOption;
	}

	public ClosedQuestionOption getChosenOption() {
        return chosenOption;
    }

    public void setChosenOption(ClosedQuestionOption chosenOption) {
        this.chosenOption = chosenOption;
    }
    
}