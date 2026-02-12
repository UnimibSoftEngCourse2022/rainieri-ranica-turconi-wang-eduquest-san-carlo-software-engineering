package it.bicocca.eduquest.dto.quiz;

public class QuestionStatsDTO {
	private double averageSuccess;
	public double getAverageSuccess() {
		return averageSuccess;
	}

	public int getTotalAnswers() {
		return totalAnswers;
	}

	public int getCorrectAnswer() {
		return correctAnswer;
	}

	private int totalAnswers;
	private int correctAnswer;
	
	public QuestionStatsDTO(double averageSuccess, int totalAnswers, int correctAnswer) {
		this.averageSuccess = averageSuccess;
		this.totalAnswers = totalAnswers;
		this.correctAnswer = correctAnswer;
	}
}
