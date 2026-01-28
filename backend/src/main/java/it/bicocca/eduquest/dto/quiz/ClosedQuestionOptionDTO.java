package it.bicocca.eduquest.dto.quiz;

public class ClosedQuestionOptionDTO {
	private Long id;
	private String text;
	private boolean isTrue;

	public ClosedQuestionOptionDTO() {
		
	}

	public ClosedQuestionOptionDTO(Long id, String text, boolean isTrue) {
		this.id = id;
		this.text = text;
		this.isTrue = isTrue;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setTrue(boolean isTrue) {
		this.isTrue = isTrue;
	}

	public String getText() {
		return text;
	}

	public boolean isTrue() {
		return isTrue;
	}
	
}


