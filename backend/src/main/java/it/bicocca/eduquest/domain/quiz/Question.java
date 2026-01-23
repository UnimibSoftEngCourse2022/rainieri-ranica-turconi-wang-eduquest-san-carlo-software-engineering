package it.bicocca.eduquest.domain.quiz;

import jakarta.persistence.*;

@Entity
@Table(name = "quizzes") 
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Question {
	
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	protected String text;
	protected String topic;
	
	@Enumerated(EnumType.STRING)
	protected Difficulty difficulty;
	
	@Embedded
	private QuestionStats stats;
	
	protected Question() {
		super();
		this.stats = new QuestionStats();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}
	
}
