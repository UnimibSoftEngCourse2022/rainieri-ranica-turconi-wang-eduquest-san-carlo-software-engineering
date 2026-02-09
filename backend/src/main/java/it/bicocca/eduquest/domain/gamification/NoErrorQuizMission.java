package it.bicocca.eduquest.domain.gamification;
import it.bicocca.eduquest.domain.answers.QuizAttempt;
import jakarta.persistence.Entity;

@Entity
public class NoErrorQuizMission extends Mission {
	private int numberOfQuizzes;
	
	public NoErrorQuizMission() {};
	
	public NoErrorQuizMission(int nQuizzes) {
		this.numberOfQuizzes = nQuizzes;
		this.title = "" + numberOfQuizzes + " quizzes without errors";
		this.description = "Complete " + numberOfQuizzes + " quizzes without any error to achieve this mission";
	}
	
	public int getProgress(int currentProgress, QuizAttempt attempt) {
		if (attempt.getScore() == attempt.getMaxScore()) {
			return currentProgress + 1;
		}
		return currentProgress;
	}
	
	public int getGoal() {
		return this.numberOfQuizzes;
	}
}