package it.bicocca.eduquest.domain.users;

import jakarta.persistence.Embeddable;

@Embeddable
public class StudentStats {
	private int quizzesCompleted = 0;
	private int totalAnswerGiven = 0;
	private int totalCorrectAnswers = 0;
	private double averageQuizzesScore = 0.0;
	
	public StudentStats() {
		// Empty constructor
	}
	
	public void updateStats(double newQuizScore, int answersGiven, int correctAnswers) {
		this.quizzesCompleted++;
		this.totalAnswerGiven += answersGiven;
		this.totalCorrectAnswers += correctAnswers;
		this.averageQuizzesScore = ((this.averageQuizzesScore * this.quizzesCompleted) + newQuizScore) / this.quizzesCompleted;
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

	public void setQuizzesCompleted(int quizzesCompleted) {
		this.quizzesCompleted = quizzesCompleted;
	}

	public void setTotalAnswerGiven(int totalAnswerGiven) {
		this.totalAnswerGiven = totalAnswerGiven;
	}

	public void setTotalCorrectAnswers(int totalCorrectAnswers) {
		this.totalCorrectAnswers = totalCorrectAnswers;
	}

	public void setAverageQuizzesScore(double averageQuizzesScore) {
		this.averageQuizzesScore = averageQuizzesScore;
	}

}
