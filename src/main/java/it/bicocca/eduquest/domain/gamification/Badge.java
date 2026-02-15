package it.bicocca.eduquest.domain.gamification;

import jakarta.persistence.*;
import it.bicocca.eduquest.domain.users.Student;
import java.time.*;

@Entity
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    
    private LocalDate obtainedDate;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    public Badge() {}

    public Badge(Mission mission, Student student) {
        this.name = mission.getTitle();
        this.description = mission.getDescription();
        this.student = student;
        this.obtainedDate = LocalDate.now();
    }

    public Badge(String name, Student student) {
        this.name = name;
        this.student = student;
        this.obtainedDate = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getDescription() {
		return description;
	}

	public LocalDate getObtainedDate() {
		return obtainedDate;
	}

	public Student getStudent() {
		return student;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setObtainedDate(LocalDate obtainedDate) {
		this.obtainedDate = obtainedDate;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

}