package it.bicocca.eduquest.dto.quiz;

public class TestDTO {

	private long id;        
    private long maxDuration;
    private int maxTries;
    private QuizDTO quiz;
    
    public TestDTO(long id, long maxDuration, int maxTries, QuizDTO quiz) {
    	this.id = id;
        this.maxDuration = maxDuration;
        this.maxTries = maxTries;
        this.quiz = quiz;
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMaxDuration() {
		return maxDuration;
	}

	public void setMaxDuration(long maxDuration) {
		this.maxDuration = maxDuration;
	}

	public int getMaxTries() {
		return maxTries;
	}

	public void setMaxTries(int maxTries) {
		this.maxTries = maxTries;
	}

	public QuizDTO getQuiz() {
		return quiz;
	}

	public void setQuiz(QuizDTO quiz) {
		this.quiz = quiz;
	}
    
}
