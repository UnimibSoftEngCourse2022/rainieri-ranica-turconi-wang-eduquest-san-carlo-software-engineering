package it.bicocca.eduquest.dto.quiz;

import java.util.Map;

public class QuizStatsDTO {
	private double averageScore;
	private int totalAttempts;
	private Map<Long, QuestionStatsDTO> statsPerQuestion;
	

	public QuizStatsDTO(double averageScore, int totalAttempts, Map<Long, QuestionStatsDTO> questionStatsDTO) {
		this.averageScore = averageScore;
		this.totalAttempts = totalAttempts;
		this.statsPerQuestion = questionStatsDTO;
	}

	public double getAverageScore() {
		return averageScore;
	}
	
	public int getTotalAttempts() {
		return totalAttempts;
	}
	
	public Map<Long, QuestionStatsDTO> getStatsPerQuestion() {
		return statsPerQuestion;
	}
}
