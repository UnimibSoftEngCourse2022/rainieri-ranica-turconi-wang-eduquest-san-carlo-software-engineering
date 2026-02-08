package it.bicocca.eduquest.domain.gamification;

import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.domain.users.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.*;
import java.time.temporal.TemporalAdjusters;

@Entity
public class MissionProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int currentCount;
    private int goal;
    private boolean isCompleted;
    
    private LocalDate assignmentDate = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student; 

    public MissionProgress() {
    	// Default constructor
    }
    
    public MissionProgress(Mission mission, Student student, int goal) {
    	this.mission = mission;
    	this.student = student;
    	this.goal = goal;
    	this.currentCount = 0;
    	this.isCompleted = false;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getCurrentCount() {
		return currentCount;
	}

	public void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
	}

	public int getGoal() {
		return goal;
	}

	public void setGoal(int goal) {
		this.goal = goal;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public Mission getMission() {
		return mission;
	}

	public void setMission(Mission mission) {
		this.mission = mission;
	}

	public User getStudent() {
		return student;
	}

	public void setStudent(User student) {
		this.student = student;
	}

	public LocalDate getAssignmentDate() {
		return assignmentDate;
	}

	public void setAssignmentDate(LocalDate assignmentDate) {
		this.assignmentDate = assignmentDate;
	}
	
	public boolean isValidForCurrentWeek() {
		if (this.assignmentDate == null) {
            return false;
        }
		LocalDate today = LocalDate.now();
		LocalDate startOfCurrentWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		return !this.assignmentDate.isBefore(startOfCurrentWeek);
	}
    
}