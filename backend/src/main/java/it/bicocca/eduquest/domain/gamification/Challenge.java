package it.bicocca.eduquest.domain.gamification;

import java.time.LocalDateTime;
import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.domain.answers.*;
import jakarta.persistence.*;

@Entity
@Table(name = "challenges")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "challenger_id", nullable = false)
    private User challenger; 

    @ManyToOne
    @JoinColumn(name = "opponent_id", nullable = false)
    private User opponent;   
   
    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;
    
    @OneToOne
    @JoinColumn(name = "challenger_attempt_id")
    private QuizAttempt challengerAttempt;

    @OneToOne
    @JoinColumn(name = "opponent_attempt_id")
    private QuizAttempt opponentAttempt;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;
    
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime completedAt;
    
    @Enumerated(EnumType.STRING)
    private ChallengeStatus status;

	public Challenge() {

	}
	
	public Challenge(User challenger, User opponent, Quiz quiz, int durationInHours) {
        this.challenger = challenger;
        this.opponent = opponent;
        this.quiz = quiz;
        this.status = ChallengeStatus.ACTIVE;
        
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusHours(durationInHours);
    }
	
	public boolean isExpired() {
        if (this.status == ChallengeStatus.COMPLETED) {
        	return false;
        }
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

	public Long getId() {
		return id;
	}

	public User getChallenger() {
		return challenger;
	}

	public User getOpponent() {
		return opponent;
	}

	public Quiz getQuiz() {
		return quiz;
	}

	public QuizAttempt getChallengerAttempt() {
		return challengerAttempt;
	}

	public QuizAttempt getOpponentAttempt() {
		return opponentAttempt;
	}

	public User getWinner() {
		return winner;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public ChallengeStatus getStatus() {
		return status;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setChallenger(User challenger) {
		this.challenger = challenger;
	}

	public void setOpponent(User opponent) {
		this.opponent = opponent;
	}

	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}

	public void setChallengerAttempt(QuizAttempt challengerAttempt) {
		this.challengerAttempt = challengerAttempt;
	}

	public void setOpponentAttempt(QuizAttempt opponentAttempt) {
		this.opponentAttempt = opponentAttempt;
	}

	public void setWinner(User winner) {
		this.winner = winner;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}

	public void setStatus(ChallengeStatus status) {
		this.status = status;
	}
    
}