package it.bicocca.eduquest.domain.users;

import jakarta.persistence.*;

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
        this.stats = new StudentStats(); // inizialize stats to null
    }

    public Student(String name, String surname, String email, String password) {
        super(name, surname, email, password);
        this.score = 0.0;
        this.role = Role.STUDENT;
        this.stats = new StudentStats();
    }   
    
    // getter e setter

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
    
    // business logic
    
    public void updateTotalScore(double points) {
        this.score += points;
    }
}