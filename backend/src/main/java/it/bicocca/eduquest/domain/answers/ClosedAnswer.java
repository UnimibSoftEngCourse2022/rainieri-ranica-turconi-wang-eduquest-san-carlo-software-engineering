package it.bicocca.eduquest.domain.answers;

import it.bicocca.eduquest.domain.quiz.ClosedQuestionOption; 
import it.bicocca.eduquest.domain.quiz.Question;
import it.bicocca.eduquest.domain.answers.QuizAttempt;
import jakarta.persistence.*;

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