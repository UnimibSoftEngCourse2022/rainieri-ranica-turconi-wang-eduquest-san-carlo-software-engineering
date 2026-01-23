package it.bicocca.eduquest.domain.answers;

import it.bicocca.eduquest.domain.quiz.ClosedQuestionOption; 
import jakarta.persistence.*;

@Entity
public class ClosedAnswer extends Answer {
  
    @ManyToOne
    @JoinColumn(name = "chosen_option_id")
    private ClosedQuestionOption chosenOption;

    public ClosedAnswer() {}

    
    public ClosedQuestionOption getChosenOption() {
        return chosenOption;
    }

    public void setChosenOption(ClosedQuestionOption chosenOption) {
        this.chosenOption = chosenOption;
    }
    
}