package it.bicocca.eduquest.domain.quiz;

import java.util.ArrayList;
import java.util.List;

import it.bicocca.eduquest.domain.users.Teacher;
import jakarta.persistence.*;

@Entity
@Table(name = "quizzes") 
@Inheritance(strategy = InheritanceType.JOINED)
public class Quiz {
	
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	private String description;
	
	@Enumerated(EnumType.STRING)
	private Difficulty difficulty;
	
	@ManyToOne
    @JoinColumn(name = "teacher_id") // Coloumn name in the DB
	private Teacher author; // Quiz author
	
	@OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

	@Embedded
	private QuizStats stats;
	
	public Quiz() {
		super();
		this.stats = new QuizStats();
	}

	public Quiz(long id, String title, String description, Difficulty difficulty, Teacher author) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.difficulty = difficulty;
		this.author = author;
		this.stats = new QuizStats();
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public Teacher getAuthor() {
		return author;
	}

	public void setAuthor(Teacher author) {
		this.author = author;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
}
