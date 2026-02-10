package it.bicocca.eduquest.dto.gamification;

import java.time.LocalDateTime;
import it.bicocca.eduquest.domain.gamification.ChallengeStatus;
import it.bicocca.eduquest.dto.user.ChallengeUserDTO;

public class ChallengeDTO {
	private Long id;
    ChallengeUserDTO challenger;
    ChallengeUserDTO opponent;
    private String quizTitle;
    private ChallengeStatus status;
    private LocalDateTime expiresAt;
    private String winnerName;
    private String winnerSurname;
    
	public ChallengeDTO() {
		this.status = ChallengeStatus.ACTIVE;
	}

	public ChallengeDTO(Long id, ChallengeUserDTO challenger, ChallengeUserDTO opponent, String quizTitle, ChallengeStatus status,
			LocalDateTime expiresAt, String winnerName, String winnerSurname) {
		this.id = id;
		this.challenger = challenger;
		this.opponent = opponent;
		this.quizTitle = quizTitle;
		this.status = status;
		this.expiresAt = expiresAt;
		this.winnerName = winnerName;
		this.winnerSurname = winnerSurname;
	} // Should use pattern builder

	public Long getId() {
		return id;
	}

	public String getQuizTitle() {
		return quizTitle;
	}

	public ChallengeStatus getStatus() {
		return status;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ChallengeUserDTO getChallenger() {
		return challenger;
	}

	public void setChallenger(ChallengeUserDTO challenger) {
		this.challenger = challenger;
	}

	public ChallengeUserDTO getOpponent() {
		return opponent;
	}

	public void setOpponent(ChallengeUserDTO opponent) {
		this.opponent = opponent;
	}

	public void setQuizTitle(String quizTitle) {
		this.quizTitle = quizTitle;
	}

	public void setStatus(ChallengeStatus status) {
		this.status = status;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public String getWinnerName() {
		return winnerName;
	}

	public String getWinnerSurname() {
		return winnerSurname;
	}

	public void setWinnerName(String winnerName) {
		this.winnerName = winnerName;
	}

	public void setWinnerSurname(String winnerSurname) {
		this.winnerSurname = winnerSurname;
	}
}	
