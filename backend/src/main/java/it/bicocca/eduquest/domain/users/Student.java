package it.bicocca.eduquest.domain.users;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User {
	private double score;

	@Embedded // Link to StudentStats
    private StudentStats stats;
	
	public Student() {
		super();
		this.role = Role.STUDENT;
		this.stats = new StudentStats();
	}

	public Student(String name, String surname, String email, String password, double score) {
		super(name, surname, email, password);
		this.score = score;
		this.role = Role.STUDENT;
		this.stats = new StudentStats();
	}	
	
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
	public void updateTotalScore(double score) {
		this.score += score;
	}
}
