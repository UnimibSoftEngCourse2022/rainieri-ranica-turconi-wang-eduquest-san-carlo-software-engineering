package it.bicocca.eduquest.domain.answers;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import it.bicocca.eduquest.domain.quiz.Question;
import it.bicocca.eduquest.domain.answers.QuizAttempt;

@Entity
@Table(name = "open_answers")
public class OpenAnswer extends Answer {

    private String text;

    public OpenAnswer() {
    	
    }

    public OpenAnswer(QuizAttempt attempt, Question question, String text) {
        super(attempt, question);
        this.text = text;
    }
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}