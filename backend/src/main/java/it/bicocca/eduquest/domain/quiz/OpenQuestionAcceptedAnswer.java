package it.bicocca.eduquest.domain.quiz;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "open_question_answers")
public class OpenQuestionAcceptedAnswer {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "answer_text")
	private String text; // The accepted answer
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_question_id")
    private OpenQuestion openQuestion;

	public OpenQuestionAcceptedAnswer() {
		
	}

	public OpenQuestionAcceptedAnswer(long id, String text, OpenQuestion openQuestion) {
		this.id = id;
		this.text = text;
		this.openQuestion = openQuestion;
	}

	public OpenQuestionAcceptedAnswer(String text) {
		this.text = text;
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

	public OpenQuestion getOpenQuestion() {
		return openQuestion;
	}

	public void setOpenQuestion(OpenQuestion openQuestion) {
		this.openQuestion = openQuestion;
	}
	
}
