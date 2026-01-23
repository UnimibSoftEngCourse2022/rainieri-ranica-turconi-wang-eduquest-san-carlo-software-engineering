package it.bicocca.eduquest.domain.answers;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double score;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();

    public QuizAttempt() {}

    public void addAnswer(Answer a) {
        this.answers.add(a);
        a.setQuizAttempt(this); 
    }

    public double calculateFinalScore() {
        // da fare
        return this.score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
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

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}