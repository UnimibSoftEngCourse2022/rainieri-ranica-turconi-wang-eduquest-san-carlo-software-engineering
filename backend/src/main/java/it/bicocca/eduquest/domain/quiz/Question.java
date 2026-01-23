package it.bicocca.eduquest.domain.quiz;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore; // Importante per il frontend

@Entity
@Table(name = "questions") // <--- CORRETTO (prima era "quizzes", sbagliato!)
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

    // --- AGGIUNTA FONDAMENTALE PER RISPETTARE IL DIAGRAMMA ---
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    @JsonIgnore
    private Quiz quiz;
	
	protected Question() {
		super();
		this.stats = new QuestionStats();
	}

    // --- GETTER E SETTER DEL NUOVO CAMPO ---
    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

	// ... (tutti gli altri getter e setter che avevi già) ...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
}