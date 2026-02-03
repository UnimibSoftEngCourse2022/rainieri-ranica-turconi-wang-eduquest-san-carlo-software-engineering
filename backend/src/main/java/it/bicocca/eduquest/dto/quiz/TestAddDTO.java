package it.bicocca.eduquest.dto.quiz;

import java.time.Duration;

public class TestAddDTO {

	private long quizId;
	private Duration maxDuration;
	private int maxTries;
	
	public long getQuizId() {
		return quizId;
	}
	public void setQuizId(long quizId) {
		this.quizId = quizId;
	}
	public Duration getDuration() {
		return maxDuration;
	}
	public void setDuration(Duration duration) {
		this.maxDuration = duration;
	}
	public int getMaxTries() {
		return maxTries;
	}
	public void setMaxTries(int maxTries) {
		this.maxTries = maxTries;
	}
		
}
