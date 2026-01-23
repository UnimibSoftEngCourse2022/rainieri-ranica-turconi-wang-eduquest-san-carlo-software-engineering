package it.bicocca.eduquest.domain.gamification;

import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.domain.quiz.Quiz; 
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timeLimit;
    private LocalDateTime startedAt;

    @ManyToOne
    @JoinColumn(name = "challenger_id")
    private User challenger; 

    @ManyToOne
    @JoinColumn(name = "opponent_id")
    private User opponent;   
   
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    public Challenge() {}

    
    public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public LocalDateTime getTimeLimit() {
		return timeLimit;
	}


	public void setTimeLimit(LocalDateTime timeLimit) {
		this.timeLimit = timeLimit;
	}


	public LocalDateTime getStartedAt() {
		return startedAt;
	}


	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}


	public User getChallenger() {
		return challenger;
	}


	public void setChallenger(User challenger) {
		this.challenger = challenger;
	}


	public User getOpponent() {
		return opponent;
	}


	public void setOpponent(User opponent) {
		this.opponent = opponent;
	}


    public boolean isCompleted() {
        
        return false;
    }
}