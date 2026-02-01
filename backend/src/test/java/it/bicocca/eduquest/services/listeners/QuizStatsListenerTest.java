package it.bicocca.eduquest.services.listeners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import it.bicocca.eduquest.domain.answers.Answer;
import it.bicocca.eduquest.domain.answers.QuizAttempt;
import it.bicocca.eduquest.domain.events.QuizCompletedEvent;
import it.bicocca.eduquest.domain.quiz.Question;
import it.bicocca.eduquest.domain.quiz.QuestionStats;
import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.quiz.QuizStats;
import it.bicocca.eduquest.repository.QuestionsRepository;
import it.bicocca.eduquest.repository.QuizRepository;

@ExtendWith(MockitoExtension.class)
class QuizStatsListenerTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionsRepository questionRepository;

    private QuizStatsListener quizStatsListener;

    @BeforeEach
    void setUp() {
        quizStatsListener = new QuizStatsListener(quizRepository, questionRepository);
    }

    @Test
    void testHandleQuizStatsUpdate_Success() {
        Quiz quiz = new Quiz();
        QuizStats quizStats = new QuizStats();
        quiz.setStats(quizStats); 

        Question q1 = new Question() {}; 
        QuestionStats qStats1 = new QuestionStats();
        q1.setStats(qStats1);

        Question q2 = new Question() {};
        QuestionStats qStats2 = new QuestionStats();
        q2.setStats(qStats2);

        Answer a1 = mock(Answer.class);
        when(a1.getQuestion()).thenReturn(q1);
        when(a1.isCorrect()).thenReturn(true);

        Answer a2 = mock(Answer.class);
        when(a2.getQuestion()).thenReturn(q2);
        when(a2.isCorrect()).thenReturn(false);

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setScore(1); 
        attempt.setAnswers(Arrays.asList(a1, a2));

        QuizCompletedEvent event = new QuizCompletedEvent(this, attempt);

        quizStatsListener.handleQuizStatsUpdate(event);

        verify(quizRepository).save(quiz);
        
        verify(questionRepository).save(q1);
        verify(questionRepository).save(q2);

    }

    @Test
    void testHandleQuizStatsUpdate_NegativeScore_NoUpdate() {
        
        Quiz quiz = new Quiz();
        QuizStats quizStatsSpy = spy(new QuizStats());
        quiz.setStats(quizStatsSpy);

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setScore(-1); 
        attempt.setAnswers(Collections.emptyList()); 

        QuizCompletedEvent event = new QuizCompletedEvent(this, attempt);

        quizStatsListener.handleQuizStatsUpdate(event);

        verify(quizRepository).save(quiz);
        verify(quizStatsSpy, never()).updateStats(anyDouble());
    }
    
    @Test
    void testHandleQuizStatsUpdate_NoAnswers() {
        Quiz quiz = new Quiz();
        quiz.setStats(new QuizStats());

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setScore(11);
        attempt.setAnswers(Collections.emptyList()); 

        QuizCompletedEvent event = new QuizCompletedEvent(this, attempt);

        quizStatsListener.handleQuizStatsUpdate(event);

        verify(quizRepository).save(quiz);
        verify(questionRepository, never()).save(any());
    }
}