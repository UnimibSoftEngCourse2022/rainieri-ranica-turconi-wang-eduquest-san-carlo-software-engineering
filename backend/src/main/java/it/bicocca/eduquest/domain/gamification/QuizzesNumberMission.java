package it.bicocca.eduquest.domain.gamification;
import it.bicocca.eduquest.domain.answers.QuizAttempt;
import it.bicocca.eduquest.domain.answers.QuizAttemptStatus;
import jakarta.persistence.Entity;

@Entity
public class QuizzesNumberMission extends Mission {
	private int numberOfQuizzes;
	
	public QuizzesNumberMission() {
		
	}
	
	public QuizzesNumberMission(int numberOfQuizzes) {
		this.numberOfQuizzes = numberOfQuizzes;
		this.title = "Complete " + numberOfQuizzes + " lessons";
		this.description = "To achieve this mission, the student must complete at least " + numberOfQuizzes + " quizzes";
	}
	
	public int getGoal() {
		return this.numberOfQuizzes;
	}
	
	public int getNumberOfQuizzes() {
		return this.numberOfQuizzes;
	}
	
	public int getProgress(int currentProgress, QuizAttempt attempt) {
		if (attempt.getStatus() == QuizAttemptStatus.COMPLETED) {
			return currentProgress + 1;
		}
		return currentProgress;
	}
}