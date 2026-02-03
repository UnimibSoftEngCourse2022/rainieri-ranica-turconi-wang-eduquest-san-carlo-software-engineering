package it.bicocca.eduquest.dto.user;

public class StudentStatsDTO {
	private int quizzesCompleted;
	private int totalAnswerGiven;
	private int totalCorrectAnswers;
	private double averageQuizzesScore;
	
	public StudentStatsDTO(int quizzesCompleted, int totalAnswerGiven, int totalCorrectAnswers, double averageQuizzesScore) {
		this.quizzesCompleted = quizzesCompleted;
		this.totalAnswerGiven = totalAnswerGiven;
		this.totalCorrectAnswers = totalCorrectAnswers;
		this.averageQuizzesScore = averageQuizzesScore;
	}
	
	public int getQuizzesCompleted() {
		return quizzesCompleted;
	}
	
	public int getTotalAnswerGiven() {
		return totalAnswerGiven;
	}
	
	public int getTotalCorrectAnswers() {
		return totalCorrectAnswers;
	}
	
	public double getAverageQuizzesScore() {
		return averageQuizzesScore;
	}
}
	