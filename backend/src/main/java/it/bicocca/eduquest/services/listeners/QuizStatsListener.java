package it.bicocca.eduquest.services.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.bicocca.eduquest.domain.answers.*;
import it.bicocca.eduquest.domain.quiz.*;
import it.bicocca.eduquest.domain.events.QuizCompletedEvent;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.repository.QuestionsRepository;

@Component
public class QuizStatsListener {
	private final QuizRepository quizRepository;
    private final QuestionsRepository questionRepository;

    public QuizStatsListener(QuizRepository quizRepository, QuestionsRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    @EventListener 
    @Transactional 
    public void handleQuizStatsUpdate(QuizCompletedEvent event) {
        QuizAttempt attempt = event.getAttempt();
        Quiz quiz = attempt.getQuiz();

        QuizStats quizStats = quiz.getStats();
        
        if (attempt.getScore() >= 0) {
             quizStats.updateStats(attempt.getScore());
        }

        quizRepository.save(quiz);

        for (Answer answer : attempt.getAnswers()) {
            Question question = answer.getQuestion();
            QuestionStats qStats = question.getStats();

            boolean isCorrect = answer.isCorrect(); 

            qStats.updateStats(isCorrect);
            
            questionRepository.save(question);
        }
    }
}
