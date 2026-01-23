package it.bicocca.eduquest.domain.users;

import jakarta.persistence.Embeddable;

@Embeddable // Link to Student
public class StudentStats {
	private int numberOfCompletedQuizzes = 0;
	private int numberOfCompletedMissions = 0;
	
	public StudentStats() {}
	
	public int getNumberOfCompletedQuizzes() {
		return numberOfCompletedQuizzes;
	}
	public void setNumberOfCompletedQuizzes(int numberOfCompletedQuizzes) {
		this.numberOfCompletedQuizzes = numberOfCompletedQuizzes;
	}
	public int getNumberOfCompletedMissions() {
		return numberOfCompletedMissions;
	}
	public void setNumberOfCompletedMissions(int numberOfCompletedMissions) {
		this.numberOfCompletedMissions = numberOfCompletedMissions;
	}
}
