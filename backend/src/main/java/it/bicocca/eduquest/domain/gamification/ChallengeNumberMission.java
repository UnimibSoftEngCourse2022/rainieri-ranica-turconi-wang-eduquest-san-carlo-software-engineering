package it.bicocca.eduquest.domain.gamification;
import it.bicocca.eduquest.domain.answers.QuizAttempt;
import jakarta.persistence.Entity;

@Entity
public class ChallengeNumberMission extends Mission {
	public int getProgress(int currentProgress, QuizAttempt attempt) {
		return -1;
	}
	
	public int getGoal() {
		return -1;
	}
}