package it.bicocca.eduquest.domain.quiz;

import it.bicocca.eduquest.domain.users.Teacher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuizTest {

    @Test
    void testQuizConstructorAndInitialization() {
        Teacher author = new Teacher("Mario", "Rossi", "mario@docenti.it", "password123");
        author.setId(10L);

        Quiz quiz = new Quiz("Matematica Base", "Quiz sulle tabelline", author);

        assertEquals("Matematica Base", quiz.getTitle());
        assertEquals("Quiz sulle tabelline", quiz.getDescription());
        assertEquals(author, quiz.getAuthor());
        
        assertNotNull(quiz.getStats());
        assertNotNull(quiz.getQuestions());
        assertTrue(quiz.getQuestions().isEmpty());
    }

    @Test
    void testAddAndRemoveQuestion() {
        Teacher author = new Teacher("Luigi", "Verdi", "luigi@test.com", "pass");
        Quiz quiz = new Quiz("Storia", "Quiz sui romani", author);
        
        OpenQuestion q1 = new OpenQuestion("Chi era Cesare?", "Storia", author, Difficulty.EASY);
        q1.setId(100L);

        OpenQuestion q2 = new OpenQuestion("Chi era Augusto?", "Storia", author, Difficulty.MEDIUM);
        q2.setId(101L);

        quiz.addQuestion(q1);
        quiz.addQuestion(q2);

        assertEquals(2, quiz.getQuestions().size());
        assertTrue(quiz.getQuestions().contains(q1));
        assertTrue(quiz.getQuestions().contains(q2));

        quiz.removeQuestion(q1);

        assertEquals(1, quiz.getQuestions().size());
        assertFalse(quiz.getQuestions().contains(q1));
        assertTrue(quiz.getQuestions().contains(q2));
    }

    @Test
    void testSettersAndGetters() {
        Teacher author = new Teacher("Anna", "Neri", "anna@test.com", "pass");
        Quiz quiz = new Quiz("Geografia", "Capitali", author);

        quiz.setDifficulty(Difficulty.HARD);
        assertEquals(Difficulty.HARD, quiz.getDifficulty());

        quiz.setMaxScore(100);
        assertEquals(100, quiz.getMaxScore());

        quiz.setTitle("Geografia Avanzata");
        assertEquals("Geografia Avanzata", quiz.getTitle());
    }
}