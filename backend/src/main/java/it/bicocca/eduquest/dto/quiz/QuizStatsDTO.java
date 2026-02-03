package it.bicocca.eduquest.dto.quiz;

public class QuizStatsDTO {
	private double averageScore;
	private int totalAttempts;
	
	public double getAverageScore() {
		return averageScore;
	}

	public int getTotalAttempts() {
		return totalAttempts;
	}

	public QuizStatsDTO(double averageScore, int totalAttempts) {
		this.averageScore = averageScore;
		this.totalAttempts = totalAttempts;
	}
}
