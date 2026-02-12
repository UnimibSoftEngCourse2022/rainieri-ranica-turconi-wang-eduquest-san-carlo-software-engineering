package it.bicocca.eduquest.dto.quizAttempt;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import it.bicocca.eduquest.domain.answers.QuizAttemptStatus;

class QuizAttemptDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        QuizAttemptDTO dto = new QuizAttemptDTO();

        long id = 1L;
        long quizId = 10L;
        String quizTitle = "History Quiz";
        long studentId = 5L;
        String studentName = "Mario";
        String studentSurname = "Rossi";
        int score = 80;
        int maxScore = 100;
        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime finishedAt = startedAt.plusMinutes(20);
        QuizAttemptStatus status = QuizAttemptStatus.COMPLETED;

        dto.setId(id);
        dto.setQuizId(quizId);
        dto.setQuizTitle(quizTitle);
        dto.setStudentId(studentId);
        dto.setStudentName(studentName);
        dto.setStudentSurname(studentSurname);
        dto.setScore(score);
        dto.setMaxScore(maxScore);
        dto.setStartedAt(startedAt);
        dto.setFinishedAt(finishedAt);
        dto.setStatus(status);

        assertEquals(id, dto.getId());
        assertEquals(quizId, dto.getQuizId());
        assertEquals(quizTitle, dto.getQuizTitle());
        assertEquals(studentId, dto.getStudentId());
        assertEquals(studentName, dto.getStudentName());
        assertEquals(studentSurname, dto.getStudentSurname());
        assertEquals(score, dto.getScore());
        assertEquals(maxScore, dto.getMaxScore());
        assertEquals(startedAt, dto.getStartedAt());
        assertEquals(finishedAt, dto.getFinishedAt());
        assertEquals(status, dto.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        long id = 2L;
        long quizId = 20L;
        String quizTitle = "Math Quiz";
        long studentId = 6L;
        String studentName = "Luigi";
        String studentSurname = "Verdi";
        int score = 90;
        int maxScore = 100;
        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime finishedAt = startedAt.plusMinutes(15);
        QuizAttemptStatus status = QuizAttemptStatus.COMPLETED; 

        QuizAttemptDTO dto = new QuizAttemptDTO(
            id, quizId, quizTitle, studentId, studentName, studentSurname, 
            score, maxScore, startedAt, finishedAt, status
        );

        assertEquals(id, dto.getId());
        assertEquals(quizId, dto.getQuizId());
        assertEquals(quizTitle, dto.getQuizTitle());
        assertEquals(studentId, dto.getStudentId());
        assertEquals(studentName, dto.getStudentName());
        assertEquals(studentSurname, dto.getStudentSurname());
        assertEquals(score, dto.getScore());
        assertEquals(maxScore, dto.getMaxScore());
        assertEquals(startedAt, dto.getStartedAt());
        assertEquals(finishedAt, dto.getFinishedAt());
        assertEquals(status, dto.getStatus());
    }
}