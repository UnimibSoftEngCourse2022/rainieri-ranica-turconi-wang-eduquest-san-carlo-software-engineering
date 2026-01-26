package it.bicocca.eduquest.domain.quiz;

import jakarta.persistence.*;

@Entity
@Table(name = "closed_question_options")
public class ClosedQuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String text;
    private boolean isTrue;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_question_id") 
    private ClosedQuestion closedQuestion;

	public ClosedQuestionOption() {

	}

	public ClosedQuestionOption(long id, String text, boolean isTrue, ClosedQuestion closedQuestion) {
		this.id = id;
		this.text = text;
		this.isTrue = isTrue;
		this.closedQuestion = closedQuestion;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isTrue() {
		return isTrue;
	}

	public void setTrue(boolean isTrue) {
		this.isTrue = isTrue;
	}

	public ClosedQuestion getClosedQuestion() {
		return closedQuestion;
	}

	public void setClosedQuestion(ClosedQuestion closedQuestion) {
		this.closedQuestion = closedQuestion;
	}
    
}