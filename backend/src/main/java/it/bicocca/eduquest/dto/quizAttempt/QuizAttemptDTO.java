package it.bicocca.eduquest.dto.quizAttempt;

import java.time.LocalDateTime;

import it.bicocca.eduquest.domain.answers.QuizAttemptStatus;

public class QuizAttemptDTO {
	private long id;
	
	private long quizId;
	private String quizTitle;
	
	private long studentId;
	private String studentName;
    private String studentSurname;
	
	private int score;
	private int maxScore;
	
	private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
 
    private QuizAttemptStatus status;

	public QuizAttemptDTO() {
		
	}

	@SuppressWarnings("java:S107")
	public QuizAttemptDTO(long id, long quizId, String quizTitle, long studentId, String studentName, String studentSurname, 
			int score, int maxScore, LocalDateTime startedAt, LocalDateTime finishedAt, QuizAttemptStatus status) {
		this.id = id;
		this.quizId = quizId;
		this.quizTitle = quizTitle;
		this.studentId = studentId;
		this.studentName = studentName;
		this.studentSurname = studentSurname;
		this.score = score;
		this.maxScore = maxScore;
		this.startedAt = startedAt;
		this.finishedAt = finishedAt;
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getQuizId() {
		return quizId;
	}

	public void setQuizId(long quizId) {
		this.quizId = quizId;
	}

	public String getQuizTitle() {
		return quizTitle;
	}

	public void setQuizTitle(String quizTitle) {
		this.quizTitle = quizTitle;
	}

	public long getStudentId() {
		return studentId;
	}

	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getStudentSurname() {
		return studentSurname;
	}

	public void setStudentSurname(String studentSurname) {
		this.studentSurname = studentSurname;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}

	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}

	public LocalDateTime getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(LocalDateTime finishedAt) {
		this.finishedAt = finishedAt;
	}

	public QuizAttemptStatus getStatus() {
		return status;
	}

	public void setStatus(QuizAttemptStatus status) {
		this.status = status;
	}
    
}

