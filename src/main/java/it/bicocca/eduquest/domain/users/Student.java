package it.bicocca.eduquest.domain.users;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id") 
public class Student extends User {
    
    private double score;

    @Embedded 
    private StudentStats stats;
    
    public Student() {
        super();
        this.role = Role.STUDENT;
        this.stats = new StudentStats();
    }

    public Student(String name, String surname, String email, String password) {
        super(name, surname, email, password);
        this.score = 0.0;
        this.role = Role.STUDENT;
        this.stats = new StudentStats();
    }   

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public StudentStats getStats() {
        return stats;
    }

    public void setStats(StudentStats stats) {
        this.stats = stats;
    }
    
    public void updateTotalScore(double points) {
        this.score += points;
    }
}