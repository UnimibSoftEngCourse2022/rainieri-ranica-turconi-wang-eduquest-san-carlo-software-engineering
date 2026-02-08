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

        for (Question question : quiz.getQuestions()) {
        	Answer givenAnswer = null;
        	for (Answer answer : attempt.getAnswers()) {
        		if (answer.getQuestion().getId() == question.getId()) {
        			givenAnswer = answer;
        			break;
        		}
        	}
        	
        	boolean isCorrect;
        	if (givenAnswer == null) {
        		isCorrect = false;
        	} else {
        		isCorrect = givenAnswer.isCorrect();
        	}
        	
        	QuestionStats globalQuestionStats = question.getStats();
        	globalQuestionStats.updateStats(isCorrect);
        	questionRepository.save(question);
        	
        	QuestionStats questionStatsInQuiz = quiz.getStats().getQuestionStats(question.getId());
        	if (questionStatsInQuiz == null) {
        		questionStatsInQuiz = new QuestionStats();
        		quiz.getStats().setQuestionStats(question.getId(), questionStatsInQuiz);
        	}
        	questionStatsInQuiz.updateStats(isCorrect);
        }
        
        quizRepository.save(quiz);
    }
}
