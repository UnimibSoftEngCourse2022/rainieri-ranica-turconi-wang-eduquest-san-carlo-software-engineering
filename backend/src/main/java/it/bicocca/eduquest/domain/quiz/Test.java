package it.bicocca.eduquest.domain.quiz;

import java.time.Duration;

import it.bicocca.eduquest.domain.users.Teacher;
import jakarta.persistence.*;

@Entity
@Table(name = "tests")
@PrimaryKeyJoinColumn(name = "test_id")
public class Test extends Quiz {
	private Duration duration;
	private int maxTries;
	
	public Test() {
		super();
	}
	
	public Test(long id, String title, String description, Difficulty difficulty, Teacher author, Duration duration, int maxTries) {
		super(id, title, description, difficulty, author);
		this.duration = duration;
		this.maxTries = maxTries;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public int getMaxTries() {
		return maxTries;
	}

	public void setMaxTries(int maxTries) {
		this.maxTries = maxTries;
	}
	
}
