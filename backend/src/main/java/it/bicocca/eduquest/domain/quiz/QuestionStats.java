package it.bicocca.eduquest.domain.quiz;

import jakarta.persistence.Embeddable;

@Embeddable
public class QuestionStats {
	private double averageSuccess;
}
