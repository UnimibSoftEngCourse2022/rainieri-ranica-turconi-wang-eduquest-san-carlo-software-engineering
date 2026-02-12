package it.bicocca.eduquest.domain.answers;

import it.bicocca.eduquest.domain.quiz.ClosedQuestionOption;
import it.bicocca.eduquest.domain.quiz.OpenQuestion;
import it.bicocca.eduquest.domain.quiz.Question;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnswersDomainTest {

    @Test
    void testOpenAnswer() {
        QuizAttempt attempt = new QuizAttempt();
        Question question = new OpenQuestion(); 
        
        OpenAnswer answer = new OpenAnswer(attempt, question, "Il testo della risposta");
        answer.setId(1L);
        answer.setCorrect(true);

        assertEquals(1L, answer.getId());
        assertEquals("Il testo della risposta", answer.getText());
        assertEquals(attempt, answer.getQuizAttempt());
        assertEquals(question, answer.getQuestion());
        assertTrue(answer.isCorrect());

        answer.setText("Nuovo testo");
        assertEquals("Nuovo testo", answer.getText());
    }

    @Test
    void testClosedAnswer() {
        QuizAttempt attempt = new QuizAttempt();
        Question question = new OpenQuestion(); 
        ClosedQuestionOption option = new ClosedQuestionOption("Opzione A", true);

        ClosedAnswer answer = new ClosedAnswer(attempt, question, option);
        
        assertEquals(option, answer.getChosenOption());
        
        ClosedQuestionOption newOption = new ClosedQuestionOption("Opzione B", false);
        answer.setChosenOption(newOption);
        
        assertEquals(newOption, answer.getChosenOption());
    }

    @Test
    void testAnswerAbstractSetters() {
        OpenAnswer answer = new OpenAnswer();
        QuizAttempt attempt = new QuizAttempt();
        Question question = new OpenQuestion();

        answer.setQuizAttempt(attempt);
        answer.setQuestion(question);
        answer.setCorrect(false);

        assertEquals(attempt, answer.getQuizAttempt());
        assertEquals(question, answer.getQuestion());
        assertFalse(answer.isCorrect());
    }

    @Test
    void testQuizAttemptStatusEnum() {
        assertEquals(QuizAttemptStatus.STARTED, QuizAttemptStatus.valueOf("STARTED"));
        assertEquals(QuizAttemptStatus.COMPLETED, QuizAttemptStatus.valueOf("COMPLETED"));
        assertEquals(2, QuizAttemptStatus.values().length);
    }
}