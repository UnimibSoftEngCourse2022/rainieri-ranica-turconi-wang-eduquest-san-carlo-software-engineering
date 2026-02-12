package it.bicocca.eduquest.domain.quiz;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuizSupportTest {

    @Test
    void testOpenQuestionAcceptedAnswer() {
        OpenQuestionAcceptedAnswer answer = new OpenQuestionAcceptedAnswer();
        answer.setId(1L);
        answer.setText("Risposta Corretta");
        
        assertEquals(1L, answer.getId());
        assertEquals("Risposta Corretta", answer.getText());

        OpenQuestionAcceptedAnswer answer2 = new OpenQuestionAcceptedAnswer("Solo Testo");
        assertEquals("Solo Testo", answer2.getText());

        OpenQuestionAcceptedAnswer answer3 = new OpenQuestionAcceptedAnswer(10L, "Full", null);
        assertEquals(10L, answer3.getId());
        assertEquals("Full", answer3.getText());
        assertNull(answer3.getOpenQuestion());
    }

    @Test
    void testDifficultyEnum() {
        assertEquals(Difficulty.EASY, Difficulty.valueOf("EASY"));
        assertEquals(Difficulty.MEDIUM, Difficulty.valueOf("MEDIUM"));
        assertEquals(Difficulty.HARD, Difficulty.valueOf("HARD"));
        assertEquals(Difficulty.UNDEFINED, Difficulty.valueOf("UNDEFINED"));
        
        assertEquals(4, Difficulty.values().length);
    }

    @Test
    void testStatsInstantiation() {
        QuizStats quizStats = new QuizStats();
        assertNotNull(quizStats);

        QuestionStats questionStats = new QuestionStats();
        assertNotNull(questionStats);
    }
}