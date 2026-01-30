package it.bicocca.eduquest.domain.answers;

import it.bicocca.eduquest.domain.quiz.Question;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "answers")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    protected Question question;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    protected QuizAttempt quizAttempt;
    
    protected boolean isCorrect;

    protected Answer() {
    	
    }
    
    protected Answer(QuizAttempt quizAttempt, Question question) {
        this.quizAttempt = quizAttempt;
        this.question = question;
    }

    public Long getId() {
        return id;
    }

    public QuizAttempt getQuizAttempt() {
        return quizAttempt;
    }

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setQuizAttempt(QuizAttempt quizAttempt) {
		this.quizAttempt = quizAttempt;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}
	
}