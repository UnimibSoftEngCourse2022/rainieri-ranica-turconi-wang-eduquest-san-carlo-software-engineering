package it.bicocca.eduquest.domain.quiz;

import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.dto.quiz.QuestionType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "questions")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Question {
	
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	protected String text;
	protected String topic;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
	protected User author;
	
	protected QuestionType questionType;
	
	@Enumerated(EnumType.STRING)
	protected Difficulty difficulty;
	
	@Embedded
	private QuestionStats stats;

    /*
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    @JsonIgnore
    private Quiz quiz; */
	
	protected Question() {
		this.stats = new QuestionStats();
	}

    /* public Question(Long id, String text, String topic, User author, QuestionType questionType, Difficulty difficulty, Quiz quiz) {
		this.id = id;
		this.text = text;
		this.topic = topic;
		this.author = author;
		this.questionType = questionType;
		this.difficulty = difficulty;
		this.stats = new QuestionStats();
		this.quiz = quiz;
	} */

    protected Question(String text, String topic, User author, QuestionType questionType, Difficulty difficulty) {
		this.text = text;
		this.topic = topic;
		this.author = author;
		this.questionType = questionType;
		this.difficulty = difficulty;
		this.stats = new QuestionStats();
	}

    public long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}
    
}