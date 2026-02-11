package it.bicocca.eduquest.domain.quiz;

import it.bicocca.eduquest.domain.users.Teacher;
import it.bicocca.eduquest.dto.quiz.QuestionType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    void testOpenQuestionInitialization() {
        Teacher author = new Teacher("Mario", "Rossi", "mario@test.com", "pass");
        OpenQuestion q = new OpenQuestion("Domanda Aperta", "Storia", author, Difficulty.MEDIUM);

        assertEquals("Domanda Aperta", q.getText());
        assertEquals("Storia", q.getTopic());
        assertEquals(author, q.getAuthor());
        assertEquals(Difficulty.MEDIUM, q.getDifficulty());
        assertEquals(QuestionType.OPENED, q.getQuestionType());
        
        assertNotNull(q.getValidAnswers());
        assertTrue(q.getValidAnswers().isEmpty());
    }

    @Test
    void testOpenQuestionLogic() {
        OpenQuestion q = new OpenQuestion();
        OpenQuestionAcceptedAnswer ans = new OpenQuestionAcceptedAnswer("Risposta");
        
        q.addAnswer(ans);

        assertEquals(1, q.getValidAnswers().size());
        assertEquals(q, ans.getOpenQuestion()); 
    }

    @Test
    void testClosedQuestionInitialization() {
        Teacher author = new Teacher();
        ClosedQuestion q = new ClosedQuestion("2+2?", "Math", author, Difficulty.EASY);

        assertEquals("2+2?", q.getText());
        assertEquals(QuestionType.CLOSED, q.getQuestionType());
        assertNotNull(q.getOptions());
        assertTrue(q.getOptions().isEmpty());
    }

    @Test
    void testClosedQuestionLogic() {
        ClosedQuestion q = new ClosedQuestion();
        ClosedQuestionOption opt1 = new ClosedQuestionOption("4", true);
        ClosedQuestionOption opt2 = new ClosedQuestionOption("5", false);

        q.addOption(opt1);
        q.addOption(opt2);

        assertEquals(2, q.getOptions().size());
        assertEquals(q, opt1.getClosedQuestion());
        assertEquals(q, opt2.getClosedQuestion());
        
        assertTrue(q.getOptions().contains(opt1));
        assertTrue(q.getOptions().get(0).isTrue());
    }

    @Test
    void testClosedQuestionOptionPOJO() {
        ClosedQuestionOption opt = new ClosedQuestionOption(1L, "Test", true, null);
        
        assertEquals(1L, opt.getId());
        assertEquals("Test", opt.getText());
        assertTrue(opt.isTrue());

        opt.setText("New Text");
        opt.setTrue(false);

        assertEquals("New Text", opt.getText());
        assertFalse(opt.isTrue());
    }
}