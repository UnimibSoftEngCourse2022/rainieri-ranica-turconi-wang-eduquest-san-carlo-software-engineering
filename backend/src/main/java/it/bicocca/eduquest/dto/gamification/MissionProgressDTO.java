package it.bicocca.eduquest.dto.gamification;

public class MissionProgressDTO {
	public long id;
	
	public MissionProgressDTO(long id, int currentCount, int goal, boolean isCompleted, MissionDTO mission, long studentId) {
		this.id = id;
		this.currentCount = currentCount;
		this.goal = goal;
		this.isCompleted = isCompleted;
		this.mission = mission;
		this.studentId = studentId;
	}

	public long getId() {
		return id;
	}

	public int getCurrentCount() {
		return currentCount;
	}

	public int getGoal() {
		return goal;
	}

	public boolean isCompleted() {
		return isCompleted;
	}
	
	public long getStudentId() {
		return studentId;
	}

	public MissionDTO getMission() {
		return mission;
	}

	public int currentCount;
	public int goal;
	private boolean isCompleted;
	public MissionDTO mission;
	private long studentId;
	
}
