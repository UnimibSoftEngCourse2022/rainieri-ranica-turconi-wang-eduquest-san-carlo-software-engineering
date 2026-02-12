package it.bicocca.eduquest.domain.quiz;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.MapKeyColumn;

@Embeddable
public class QuizStats {
	private double averageScore = 0.0;
	private int totalAttempts = 0;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "question_id")
	private Map<Long, QuestionStats> statsPerQuestion = new HashMap<>();
	
	public QuizStats() {
		// Empty constructor
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
	
	public Map<Long, QuestionStats> getStatsPerQuestion() {
		return this.statsPerQuestion;
	}
	
	public QuestionStats getQuestionStats(long questionId) {
		return this.statsPerQuestion.get(questionId);
	}
	
	public void setQuestionStats(long questionId, QuestionStats questionStats) {
		this.statsPerQuestion.put(questionId, questionStats);
	}
}
