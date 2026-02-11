package it.bicocca.eduquest.dto.user;

public class ChallengeUserDTO {
	private long id;
	private String name;
	private String surname;
	private boolean hasCompletedQuiz;
	
	public ChallengeUserDTO(long id, String name, String surname, boolean hasCompletedQuiz) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.hasCompletedQuiz = hasCompletedQuiz;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public boolean isHasCompletedQuiz() {
		return hasCompletedQuiz;
	}

	public void setHasCompletedQuiz(boolean hasCompletedQuiz) {
		this.hasCompletedQuiz = hasCompletedQuiz;
	}
}
