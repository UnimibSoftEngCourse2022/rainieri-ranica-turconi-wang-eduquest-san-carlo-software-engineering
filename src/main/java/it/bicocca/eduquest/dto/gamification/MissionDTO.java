package it.bicocca.eduquest.dto.gamification;

public class MissionDTO {
	private long id;
	private String title;
	private String description;
	private int goal;
	
	public MissionDTO(long id, String title, String description, int goal) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.goal = goal;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getGoal() {
		return goal;
	}

	public void setGoal(int goal) {
		this.goal = goal;
	}
}
