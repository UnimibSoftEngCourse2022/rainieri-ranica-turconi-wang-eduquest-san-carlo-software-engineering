package it.bicocca.eduquest.dto.quiz;

public class TestDTO {
    private long id;
    private long maxDuration;
    private int maxTries;
    private QuizDTO quiz;
    
    private Double testAverageScore;
    private int testTotalAttempts;

    public TestDTO(long id, long maxDuration, int maxTries, QuizDTO quiz) {
        this.id = id;
        this.maxDuration = maxDuration;
        this.maxTries = maxTries;
        this.quiz = quiz;
        this.testAverageScore = 0.0;
        this.testTotalAttempts = 0;
    }

    public TestDTO() {
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

    public Double getTestAverageScore() {
        return testAverageScore;
    }

    public void setTestAverageScore(Double testAverageScore) {
        this.testAverageScore = testAverageScore;
    }

    public int getTestTotalAttempts() {
        return testTotalAttempts;
    }

    public void setTestTotalAttempts(int testTotalAttempts) {
        this.testTotalAttempts = testTotalAttempts;
    }
}