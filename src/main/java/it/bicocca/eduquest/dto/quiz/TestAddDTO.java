package it.bicocca.eduquest.dto.quiz;

public class TestAddDTO {

	private Long quizId;             
	private Integer maxDurationMinutes; 
	private int maxTries;         
	
	public Long getQuizId() {
		return quizId;
	}
	public void setQuizId(Long quizId) {
		this.quizId = quizId;
	}
	
	public Integer getMaxDurationMinutes() {
		return maxDurationMinutes;
	}
	public void setMaxDurationMinutes(Integer maxDurationMinutes) {
		this.maxDurationMinutes = maxDurationMinutes;
	}
	
	public int getMaxTries() {
		return maxTries;
	}
	public void setMaxTries(Integer maxTries) {
		this.maxTries = maxTries;
	}
}