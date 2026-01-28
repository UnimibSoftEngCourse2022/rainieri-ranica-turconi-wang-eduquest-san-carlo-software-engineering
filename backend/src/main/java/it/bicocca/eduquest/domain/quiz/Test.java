package it.bicocca.eduquest.domain.quiz;

import java.time.Duration;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tests")
public class Test {
	@Id
	private long id;

	@ManyToOne
	@JoinColumn(name = "quiz_id")
	private Quiz quiz;
	private Duration duration;
	private int maxTries;
	
	public Test() {
		super();
	}
	
	public Test(long id, Quiz quiz, Duration duration, int maxTries) {
		this.id = id;
		this.quiz = quiz;
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

	public Quiz getQuiz() {
		return quiz;
	}

	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}
}
