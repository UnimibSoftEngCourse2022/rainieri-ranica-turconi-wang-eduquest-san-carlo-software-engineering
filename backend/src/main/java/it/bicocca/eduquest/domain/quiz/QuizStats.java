package it.bicocca.eduquest.domain.quiz;

import jakarta.persistence.Embeddable;

@Embeddable
public class QuizStats {
	private double averageScore = 0.0;
	private int totalAttempts = 0;
	
	public QuizStats() {

	}
	
	public void updateStats(double newScore) {
		double currentTotalScore = this.averageScore * this.totalAttempts;
        this.totalAttempts++;
        this.averageScore = (currentTotalScore + newScore) / this.totalAttempts;
	}

	public double getAverageScore() {
		return averageScore;
	}

	public int getTotalAttempts() {
		return totalAttempts;
	}

	public void setAverageScore(double averageScore) {
		this.averageScore = averageScore;
	}

	public void setTotalAttempts(int totalAttempts) {
		this.totalAttempts = totalAttempts;
	}
	
}
