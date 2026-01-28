package it.bicocca.eduquest.domain.quiz;

import it.bicocca.eduquest.domain.users.Teacher;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

class TestTest {

    @Test
    void testTestInitialization() {
        Teacher author = new Teacher();
        Quiz quiz = new Quiz("Quiz 1", "Desc", author);
        Duration duration = Duration.ofMinutes(60);

        it.bicocca.eduquest.domain.quiz.Test exam = new it.bicocca.eduquest.domain.quiz.Test(1L, quiz, duration, 3);

        assertEquals(quiz, exam.getQuiz());
        assertEquals(duration, exam.getDuration());
        assertEquals(3, exam.getMaxTries());
    }

    @Test
    void testSettersAndGetters() {
        it.bicocca.eduquest.domain.quiz.Test exam = new it.bicocca.eduquest.domain.quiz.Test();
        
        Teacher author = new Teacher();
        Quiz newQuiz = new Quiz("Quiz 2", "Desc", author);
        Duration newDuration = Duration.ofMinutes(90);

        exam.setQuiz(newQuiz);
        exam.setDuration(newDuration);
        exam.setMaxTries(5);

        assertEquals(newQuiz, exam.getQuiz());
        assertEquals(newDuration, exam.getDuration());
        assertEquals(5, exam.getMaxTries());
    }
}