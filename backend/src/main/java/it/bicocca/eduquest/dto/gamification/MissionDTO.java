package it.bicocca.eduquest.dto.gamification;

public class MissionDTO {
	public long id;
	public String title;
	public String description;
	public int goal;
	
	public MissionDTO(long id, String title, String description, int goal) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.goal = goal;
	}
}
