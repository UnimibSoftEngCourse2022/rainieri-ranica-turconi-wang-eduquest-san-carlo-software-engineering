package it.bicocca.eduquest.domain.answers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.users.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    
    private int score;
    private int maxScore;
    
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    
    @Enumerated(EnumType.STRING)
    private QuizAttemptStatus status;

    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();

    public QuizAttempt() {
    	
    }

    public QuizAttempt(User student, Quiz quiz) {
		this.student = student;
		this.quiz = quiz;
		this.startedAt = LocalDateTime.now();
		this.status = QuizAttemptStatus.STARTED;
		this.score = 0;
		this.maxScore = 0;
	}

    public void closeAttempt(int totalScore, int maxPossibleScore) {
    	this.score = totalScore;
    	this.maxScore = maxPossibleScore;
    	this.finishedAt = LocalDateTime.now();
    	this.status = QuizAttemptStatus.COMPLETED;
    
    }

	public void addAnswer(Answer a) {
        this.answers.add(a);
        a.setQuizAttempt(this); 
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

	public User getStudent() {
		return student;
	}

	public void setStudent(User student) {
		this.student = student;
	}

	public Quiz getQuiz() {
		return quiz;
	}

	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}

	public QuizAttemptStatus getStatus() {
		return status;
	}

	public void setStatus(QuizAttemptStatus status) {
		this.status = status;
	}
}