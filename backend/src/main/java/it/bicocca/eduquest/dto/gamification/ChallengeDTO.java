package it.bicocca.eduquest.dto.gamification;

import java.time.LocalDateTime;
import it.bicocca.eduquest.domain.gamification.ChallengeStatus;

public class ChallengeDTO {
	private Long id;
    private Long challengerId; 
    private String challengerName;
    private String challengerSurname;
    private Long opponentId;
    private String opponentName;
    private String opponentSurname;
    private String quizTitle;
    private ChallengeStatus status;
    private LocalDateTime expiresAt;
    private String winnerName;
    private String winnerSurname;
    
	public ChallengeDTO() {
		this.status = ChallengeStatus.ACTIVE;
	}

	public ChallengeDTO(Long id, Long challengerId, String challengerName, String challengerSurname, Long opponentId,
			String opponentName, String opponentSurname, String quizTitle, ChallengeStatus status,
			LocalDateTime expiresAt, String winnerName, String winnerSurname) {
		this.id = id;
		this.challengerId = challengerId;
		this.challengerName = challengerName;
		this.challengerSurname = challengerSurname;
		this.opponentId = opponentId;
		this.opponentName = opponentName;
		this.opponentSurname = opponentSurname;
		this.quizTitle = quizTitle;
		this.status = status;
		this.expiresAt = expiresAt;
		this.winnerName = winnerName;
		this.winnerSurname = winnerSurname;
	}

	public Long getId() {
		return id;
	}

	public Long getChallengerId() {
		return challengerId;
	}

	public String getChallengerName() {
		return challengerName;
	}

	public String getChallengerSurname() {
		return challengerSurname;
	}

	public Long getOpponentId() {
		return opponentId;
	}

	public String getOpponentName() {
		return opponentName;
	}

	public String getOpponentSurname() {
		return opponentSurname;
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

	public void setChallengerId(Long challengerId) {
		this.challengerId = challengerId;
	}

	public void setChallengerName(String challengerName) {
		this.challengerName = challengerName;
	}

	public void setChallengerSurname(String challengerSurname) {
		this.challengerSurname = challengerSurname;
	}

	public void setOpponentId(Long opponentId) {
		this.opponentId = opponentId;
	}

	public void setOpponentName(String opponentName) {
		this.opponentName = opponentName;
	}

	public void setOpponentSurname(String opponentSurname) {
		this.opponentSurname = opponentSurname;
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
