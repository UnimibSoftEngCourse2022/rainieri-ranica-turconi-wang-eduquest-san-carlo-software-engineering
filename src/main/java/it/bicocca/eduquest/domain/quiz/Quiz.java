package it.bicocca.eduquest.domain.quiz;

import java.util.ArrayList;
import java.util.List;

import it.bicocca.eduquest.domain.users.Teacher;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "quizzes") 
@Inheritance(strategy = InheritanceType.JOINED)
public class Quiz {
	
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String title;
	private String description;
	
	@Enumerated(EnumType.STRING)
	private Difficulty difficulty = Difficulty.UNDEFINED;
	
	private int maxScore;
	
	@ManyToOne
    @JoinColumn(name = "teacher_id") // Coloumn name in the DB
	private Teacher author; // Quiz author
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable( name = "quiz_questions", joinColumns = @JoinColumn(name = "quiz_id"), inverseJoinColumns = @JoinColumn(name = "question_id"))
    private List<Question> questions = new ArrayList<>();

	@Embedded
	private QuizStats stats;
	
	private boolean isPublic = false;
	
	public Quiz() {
		super();
		this.stats = new QuizStats();
	}

	public Quiz(long id, String title, String description, Teacher author) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.author = author;
		this.stats = new QuizStats();
	}

	public Quiz(String title, String description, Teacher author) {
		this.title = title;
		this.description = description;
		this.author = author;
		this.stats = new QuizStats();
	}
	
	public void recalculateDifficulty() {
		if (questions == null || questions.isEmpty()) {
			this.difficulty = Difficulty.UNDEFINED;
			return;
		}
		
		double totalDifficultyValue = 0;
		for (Question q : questions) {
			totalDifficultyValue += getDifficultyValue(q.getDifficulty());
		}
		
		double averageDifficultyValue = totalDifficultyValue/questions.size();
		
		if (averageDifficultyValue <= 1.5) {
            this.difficulty = Difficulty.EASY;
        } else if (averageDifficultyValue <= 2.5) {
            this.difficulty = Difficulty.MEDIUM;
        } else {
            this.difficulty = Difficulty.HARD;
        }
				
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

	public long getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}

	public QuizStats getStats() {
		return stats;
	}

	public void setStats(QuizStats stats) {
		this.stats = stats;
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
	
	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void addQuestion(Question question) {
		this.questions.add(question);
	}
	
	public void removeQuestion(Question question) {
		this.questions.remove(question);
	}
	
	private int getDifficultyValue(Difficulty difficulty) {
		switch(difficulty) {
			case HARD: return 3;
			case MEDIUM: return 2;
			default: return 1;
		}
	}

}
