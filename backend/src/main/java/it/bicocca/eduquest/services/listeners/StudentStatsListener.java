package it.bicocca.eduquest.services.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.bicocca.eduquest.domain.answers.*;
import it.bicocca.eduquest.domain.users.*;
import it.bicocca.eduquest.domain.events.QuizCompletedEvent;
import it.bicocca.eduquest.repository.UsersRepository;
import org.hibernate.Hibernate;

@Component
public class StudentStatsListener {

    private final UsersRepository usersRepository;

    public StudentStatsListener(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
	}

	@EventListener
    @Transactional
    public void handleUserStatsUpdate(QuizCompletedEvent event) {
        QuizAttempt attempt = event.getAttempt();
        User student = (User)Hibernate.unproxy(attempt.getStudent());

        double score;

        if (attempt.getScore() >= 0) {
            score = attempt.getScore();
        } else {
            score = 0.0;
        }
  
        int answersInThisQuiz = 0;
        int correctInThisQuiz = 0;

        if (attempt.getAnswers() != null) {
            answersInThisQuiz = attempt.getAnswers().size();

            for (Answer ans : attempt.getAnswers()) {
                if (ans.isCorrect()) { 
                    correctInThisQuiz++;
                }
            }
        }

        StudentStats stats = ((Student)student).getStats();
        
        if (stats == null) {
            stats = new StudentStats();
            ((Student)student).setStats(stats);
        }

        stats.updateStats(score, answersInThisQuiz, correctInThisQuiz);

        usersRepository.save(student);
        
    }
}