package it.bicocca.eduquest.domain.quiz;

import jakarta.persistence.Embeddable;

@Embeddable
public class QuestionStats {
	private double averageSuccess = 0.0;
	private int totalAnswers = 0;
	private int correctAnswer = 0;
	
	public QuestionStats() {
		// Empty constructor
	}
	
	public void updateStats(boolean isCorrect) {
		this.totalAnswers++;
		if (isCorrect) {
			this.correctAnswer++;
		}
		if (this.totalAnswers > 0) {
			this.averageSuccess = (double) this.totalAnswers / this.correctAnswer;
		}
	}

	public double getAverageSuccess() {
		return averageSuccess;
	}

	public int getTotalAnswers() {
		return totalAnswers;
	}

	public int getCorrectAnswer() {
		return correctAnswer;
	}

	public void setAverageSuccess(double averageSuccess) {
		this.averageSuccess = averageSuccess;
	}

	public void setTotalAnswers(int totalAnswers) {
		this.totalAnswers = totalAnswers;
	}

	public void setCorrectAnswer(int correctAnswer) {
		this.correctAnswer = correctAnswer;
	}
	
}