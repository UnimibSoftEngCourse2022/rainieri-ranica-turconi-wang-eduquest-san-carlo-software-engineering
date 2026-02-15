package it.bicocca.eduquest.domain.answers;

import it.bicocca.eduquest.domain.quiz.OpenQuestion;
import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.users.Student;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class QuizAttemptTest {

    @Test
    void testInitialization() {
        Student student = new Student();
        Quiz quiz = new Quiz();
        
        QuizAttempt attempt = new QuizAttempt(student, quiz);

        assertEquals(student, attempt.getStudent());
        assertEquals(quiz, attempt.getQuiz());
        assertEquals(QuizAttemptStatus.STARTED, attempt.getStatus());
        assertEquals(0, attempt.getScore());
        assertEquals(0, attempt.getMaxScore());
        assertNotNull(attempt.getStartedAt());
        assertNotNull(attempt.getAnswers());
        assertTrue(attempt.getAnswers().isEmpty());
    }

    @Test
    void testCloseAttemptLogic() {
        QuizAttempt attempt = new QuizAttempt(new Student(), new Quiz());
        
        attempt.closeAttempt(85, 100);

        assertEquals(QuizAttemptStatus.COMPLETED, attempt.getStatus());
        assertEquals(85, attempt.getScore());
        assertEquals(100, attempt.getMaxScore());
        assertNotNull(attempt.getFinishedAt());
    }

    @Test
    void testAddAnswerRelationship() {
        QuizAttempt attempt = new QuizAttempt();
        OpenQuestion question = new OpenQuestion();
        
        OpenAnswer answer = new OpenAnswer(attempt, question, "Risposta data");

        attempt.addAnswer(answer);

        assertEquals(1, attempt.getAnswers().size());
        assertEquals(answer, attempt.getAnswers().get(0));
        assertEquals(attempt, answer.getQuizAttempt());
    }

    @Test
    void testSettersAndGetters() {
        QuizAttempt attempt = new QuizAttempt();
        LocalDateTime time = LocalDateTime.now();
        
        attempt.setId(10L);
        attempt.setStartedAt(time);
        attempt.setFinishedAt(time.plusMinutes(30));
        attempt.setScore(50);
        attempt.setMaxScore(60);
        attempt.setStatus(QuizAttemptStatus.COMPLETED);

        assertEquals(10L, attempt.getId());
        assertEquals(time, attempt.getStartedAt());
        assertEquals(time.plusMinutes(30), attempt.getFinishedAt());
        assertEquals(50, attempt.getScore());
        assertEquals(60, attempt.getMaxScore());
        assertEquals(QuizAttemptStatus.COMPLETED, attempt.getStatus());
    }
}