package it.bicocca.eduquest.dto.quiz;

public class ClosedQuestionOptionDTO {
	private final String text;
	private final boolean isTrue;
	
	public ClosedQuestionOptionDTO(String text, boolean isTrue) {
		this.text = text;
		this.isTrue = isTrue;
	}
}
