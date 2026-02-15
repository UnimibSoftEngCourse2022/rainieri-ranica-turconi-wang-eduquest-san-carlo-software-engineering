package it.bicocca.eduquest.dto.gamification;

public class ChallengeCreateDTO {
	private Long opponentId;
    private Long quizId;
    private int durationInHours;
	
    public ChallengeCreateDTO() {
    	// Empty constructor
	}

	public Long getOpponentId() {
		return opponentId;
	}

	public Long getQuizId() {
		return quizId;
	}

	public int getDurationInHours() {
		return durationInHours;
	}

	public void setOpponentId(Long opponentId) {
		this.opponentId = opponentId;
	}

	public void setQuizId(Long quizId) {
		this.quizId = quizId;
	}

	public void setDurationInHours(int durationInHours) {
		this.durationInHours = durationInHours;
	}
    
}
