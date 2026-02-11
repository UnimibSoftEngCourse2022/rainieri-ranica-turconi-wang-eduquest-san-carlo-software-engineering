package it.bicocca.eduquest.domain.gamification;
import it.bicocca.eduquest.domain.answers.QuizAttempt;
import jakarta.persistence.Entity;

@Entity
public class ChallengeNumberMission extends Mission {
	private int numberOfChallengesToWin;

	public ChallengeNumberMission() {

	}

	public ChallengeNumberMission(int numberOfChallengesToWin) {
		this.numberOfChallengesToWin = numberOfChallengesToWin;
		this.title = "" + numberOfChallengesToWin + " challenges to win";
		this.description = "Win " + numberOfChallengesToWin + " challenges to achieve this mission";
	}

	@Override
	public int getGoal() {
		return this.numberOfChallengesToWin;
	}

	@Override
	public int getProgress(int currentProgress, QuizAttempt attempt) {
		return currentProgress;
	}
	
	
}