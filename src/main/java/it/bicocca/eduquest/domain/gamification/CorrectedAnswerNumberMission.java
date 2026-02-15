package it.bicocca.eduquest.domain.gamification;

import it.bicocca.eduquest.domain.answers.QuizAttempt;
import jakarta.persistence.*;

@Entity
public class CorrectedAnswerNumberMission extends Mission {
	private int numberOfCorrectedAnswer;

	public CorrectedAnswerNumberMission() {

	}

	public CorrectedAnswerNumberMission(int numberOfCorrectedAnswer) {
		this.numberOfCorrectedAnswer = numberOfCorrectedAnswer;
		this.title = "" + numberOfCorrectedAnswer + " correct answers";
		this.description = "Give " + numberOfCorrectedAnswer + " correct answers to achieve this mission";
	}

	@Override
	public int getGoal() {
		return numberOfCorrectedAnswer;
	}

	@Override
	public int getProgress(int currentProgress, QuizAttempt attempt) {
		return currentProgress + attempt.getScore();
	}
	
	
}
