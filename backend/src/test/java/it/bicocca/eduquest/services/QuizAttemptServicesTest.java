package it.bicocca.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.bicocca.eduquest.domain.answers.*;
import it.bicocca.eduquest.domain.quiz.*;
import it.bicocca.eduquest.domain.users.*;
import it.bicocca.eduquest.dto.quiz.QuestionType;
import it.bicocca.eduquest.dto.quizAttempt.AnswerDTO;
import it.bicocca.eduquest.dto.quizAttempt.QuizAttemptDTO;
import it.bicocca.eduquest.dto.quizAttempt.QuizSessionDTO;
import it.bicocca.eduquest.repository.*;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class QuizAttemptServicesTest {

    @Mock private AnswersRepository answersRepository;
    @Mock private QuizAttemptsRepository quizAttemptsRepository;
    @Mock private QuizRepository quizRepository;
    @Mock private UsersRepository usersRepository;
    @Mock private QuestionsRepository questionsRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private QuizAttemptServices quizAttemptServices;

    @Test
    void testStartQuiz_NewAttempt_Success() {
        long quizId = 1L;
        long studentId = 2L;

        Student student = new Student();
        student.setId(studentId);

        Teacher author = new Teacher();
        author.setId(99L);

        Quiz quiz = new Quiz(quizId, "Title", "Desc", author);
        
        OpenQuestion q1 = new OpenQuestion();
        q1.setId(10L);
        q1.setQuestionType(QuestionType.OPENED);
        q1.setAuthor(author);
        
        ClosedQuestion q2 = new ClosedQuestion();
        q2.setId(20L);
        q2.setQuestionType(QuestionType.CLOSED);
        q2.setAuthor(author);
        ClosedQuestionOption opt = new ClosedQuestionOption("A", true);
        opt.setId(100L);
        q2.addOption(opt);

        quiz.addQuestion(q1);
        quiz.addQuestion(q2);

        when(usersRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(quizAttemptsRepository.findByStudentAndQuizAndStatus(student, quiz, QuizAttemptStatus.STARTED))
                .thenReturn(Optional.empty());

        when(quizAttemptsRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> {
            QuizAttempt saved = invocation.getArgument(0);
            saved.setId(500L); 
            return saved;
        });

        QuizSessionDTO result = quizAttemptServices.startQuiz(quizId, studentId);

        assertNotNull(result);
        assertEquals(500L, result.getAttemptId());
        assertEquals(2, result.getQuestions().size());
        verify(quizAttemptsRepository).save(any(QuizAttempt.class));
    }

    @Test
    void testStartQuiz_ResumeAttempt_Success() {
        long quizId = 1L;
        long studentId = 2L;
        Student student = new Student();
        Quiz quiz = new Quiz(quizId, "Title", "Desc", new Teacher());
        
        QuizAttempt existingAttempt = new QuizAttempt(student, quiz);
        existingAttempt.setId(500L);
        
        OpenQuestion q = new OpenQuestion(); 
        q.setId(10L); q.setAuthor(new Teacher()); q.setQuestionType(QuestionType.OPENED);
        OpenAnswer existingAns = new OpenAnswer(existingAttempt, q, "Old Answer");
        existingAttempt.addAnswer(existingAns);

        when(usersRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(quizAttemptsRepository.findByStudentAndQuizAndStatus(student, quiz, QuizAttemptStatus.STARTED))
                .thenReturn(Optional.of(existingAttempt));

        QuizSessionDTO result = quizAttemptServices.startQuiz(quizId, studentId);

        assertEquals(500L, result.getAttemptId());
        assertEquals(1, result.getExistingAnswers().size()); 
        verify(quizAttemptsRepository, never()).save(any(QuizAttempt.class));
    }

    @Test
    void testStartQuiz_Failures() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> quizAttemptServices.startQuiz(1L, 1L));

        Teacher teacher = new Teacher();
        when(usersRepository.findById(2L)).thenReturn(Optional.of(teacher));
        RuntimeException ex2 = assertThrows(RuntimeException.class, () -> quizAttemptServices.startQuiz(1L, 2L));
        assertEquals("Given ID is associated to a Teacher, not a Student", ex2.getMessage());

        Student student = new Student();
        when(usersRepository.findById(3L)).thenReturn(Optional.of(student));
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> quizAttemptServices.startQuiz(99L, 3L));
    }

    @Test
    void testSaveSingleAnswer_Open_Success_CreateNew() {
        long attemptId = 1L;
        long questionId = 10L;
        long userId = 50L; // <--- Nuovo parametro

        Student student = new Student(); 
        student.setId(userId); // <--- Setup studente

        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(attemptId);
        attempt.setStatus(QuizAttemptStatus.STARTED);
        attempt.setStudent(student); // <--- Importante!

        OpenQuestion question = new OpenQuestion();
        question.setId(questionId);

        AnswerDTO dto = new AnswerDTO();
        dto.setQuizAttemptId(attemptId);
        dto.setQuestionId(questionId);
        dto.setTextOpenAnswer("Risposta aperta");

        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(questionsRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(answersRepository.findByQuizAttemptAndQuestion(attempt, question)).thenReturn(Optional.empty());
        
        when(answersRepository.save(any(Answer.class))).thenAnswer(i -> i.getArgument(0));

        // Passiamo userId al metodo
        AnswerDTO result = quizAttemptServices.saveSingleAnswer(dto, userId);

        assertEquals("Risposta aperta", result.getTextOpenAnswer());
        verify(answersRepository).save(any(OpenAnswer.class));
    }

    @Test
    void testSaveSingleAnswer_Closed_Success_UpdateExisting() {
        long attemptId = 1L;
        long questionId = 20L;
        long optionId = 100L;
        long userId = 50L;

        Student student = new Student(); 
        student.setId(userId);

        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(attemptId);
        attempt.setStatus(QuizAttemptStatus.STARTED);
        attempt.setStudent(student); // Setup studente

        ClosedQuestion question = new ClosedQuestion();
        question.setId(questionId); 
        
        ClosedQuestionOption option = new ClosedQuestionOption("Correct Option", true);
        option.setId(optionId);
        question.addOption(option);

        ClosedAnswer existingAnswer = new ClosedAnswer(attempt, question, null);
        
        AnswerDTO dto = new AnswerDTO();
        dto.setQuizAttemptId(attemptId);
        dto.setQuestionId(questionId);
        dto.setSelectedOptionId(optionId);

        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(questionsRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(answersRepository.findByQuizAttemptAndQuestion(attempt, question)).thenReturn(Optional.of(existingAnswer));
        when(answersRepository.save(any(Answer.class))).thenAnswer(i -> i.getArgument(0));

        // Passiamo userId
        AnswerDTO result = quizAttemptServices.saveSingleAnswer(dto, userId);

        assertEquals(optionId, result.getSelectedOptionId());
        verify(answersRepository).save(existingAnswer);
    }
    
    @Test
    void testSaveSingleAnswer_Security_NotYourAttempt() {
        long attemptId = 1L;
        long ownerId = 10L;
        long hackerId = 666L;

        Student owner = new Student(); owner.setId(ownerId);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(attemptId);
        attempt.setStatus(QuizAttemptStatus.STARTED);
        attempt.setStudent(owner); // Il proprietario è ID 10

        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        AnswerDTO dto = new AnswerDTO();
        dto.setQuizAttemptId(attemptId);

        // Provo a salvare con l'ID dell'hacker (666)
        RuntimeException ex = assertThrows(RuntimeException.class, () -> 
            quizAttemptServices.saveSingleAnswer(dto, hackerId)
        );
        assertTrue(ex.getMessage().contains("not your attempt"));
    }

    @Test
    void testSaveSingleAnswer_Failures() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);
        
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(1L);
        attempt.setStudent(student); // Setup studente

        when(quizAttemptsRepository.findById(1L)).thenReturn(Optional.of(attempt));

        // Test 1: Quiz Completed
        attempt.setStatus(QuizAttemptStatus.COMPLETED);
        AnswerDTO dto = new AnswerDTO(); dto.setQuizAttemptId(1L);
        assertThrows(RuntimeException.class, () -> quizAttemptServices.saveSingleAnswer(dto, userId));

        // Test 2: Inconsistency
        attempt.setStatus(QuizAttemptStatus.STARTED);
        ClosedQuestion closedQ = new ClosedQuestion(); closedQ.setId(2L);
        when(questionsRepository.findById(2L)).thenReturn(Optional.of(closedQ));
        
        OpenAnswer wrongTypeAnswer = new OpenAnswer();
        when(answersRepository.findByQuizAttemptAndQuestion(attempt, closedQ)).thenReturn(Optional.of(wrongTypeAnswer));
        
        dto.setQuestionId(2L);
        dto.setSelectedOptionId(5L);
        
        RuntimeException ex = assertThrows(RuntimeException.class, () -> quizAttemptServices.saveSingleAnswer(dto, userId));
        assertEquals("Question/answer inconsistency", ex.getMessage());
    }

    @Test
    void testSaveSingleAnswer_Closed_InvalidOption() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);

        QuizAttempt attempt = new QuizAttempt(); 
        attempt.setId(1L);
        attempt.setStatus(QuizAttemptStatus.STARTED);
        attempt.setStudent(student);

        ClosedQuestion question = new ClosedQuestion(); 
        
        AnswerDTO dto = new AnswerDTO();
        dto.setQuizAttemptId(1L);
        dto.setQuestionId(2L);
        dto.setSelectedOptionId(999L); 

        when(quizAttemptsRepository.findById(1L)).thenReturn(Optional.of(attempt));
        when(questionsRepository.findById(2L)).thenReturn(Optional.of(question));
        when(answersRepository.findByQuizAttemptAndQuestion(attempt, question)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> quizAttemptServices.saveSingleAnswer(dto, userId));
    }

    @Test
    void testCompleteQuizAttempt_Scoring() {
        long attemptId = 1L;
        
        Student student = new Student(); 
        student.setId(10L); // ID settato
        student.setName("Mario"); 
        student.setSurname("Rossi");

        Quiz quiz = new Quiz(); 
        quiz.setId(20L); // ID settato
        quiz.setTitle("Final Quiz");
        
        QuizAttempt attempt = new QuizAttempt(student, quiz);
        attempt.setId(attemptId);
        attempt.setStatus(QuizAttemptStatus.STARTED);

        ClosedQuestion q1 = new ClosedQuestion();
        ClosedQuestionOption optCorrect = new ClosedQuestionOption("A", true);
        ClosedAnswer a1 = new ClosedAnswer(attempt, q1, optCorrect);
        
        OpenQuestion q2 = new OpenQuestion();
        OpenQuestionAcceptedAnswer valid = new OpenQuestionAcceptedAnswer("Paris");
        q2.addAnswer(valid);
        OpenAnswer a2 = new OpenAnswer(attempt, q2, " paris "); 

        OpenQuestion q3 = new OpenQuestion();
        OpenQuestionAcceptedAnswer validQ3 = new OpenQuestionAcceptedAnswer("Rome");
        q3.addAnswer(validQ3); 
        OpenAnswer a3 = new OpenAnswer(attempt, q3, "Berlin");

        attempt.setAnswers(Arrays.asList(a1, a2, a3));

        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        // Passiamo l'ID corretto (10L)
        QuizAttemptDTO result = quizAttemptServices.completeQuizAttempt(attemptId, 10L);

        assertEquals(QuizAttemptStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getFinishedAt());
        assertEquals(3, result.getMaxScore());
        assertEquals(2, result.getScore());
        
        assertTrue(a1.isCorrect());
        assertTrue(a2.isCorrect());
        assertFalse(a3.isCorrect());
    }
    
    @Test
    void testCompleteQuizAttempt_FailIfAlreadyCompleted() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);
        
        QuizAttempt attempt = new QuizAttempt();
        attempt.setStatus(QuizAttemptStatus.COMPLETED);
        attempt.setStudent(student);
        
        when(quizAttemptsRepository.findById(1L)).thenReturn(Optional.of(attempt));
        
        assertThrows(RuntimeException.class, () -> quizAttemptServices.completeQuizAttempt(1L, userId));
    }
    
    @Test
    void testCompleteQuizAttempt_Security_NotYourAttempt() {
        long attemptId = 1L;
        long ownerId = 10L;
        long hackerId = 999L;

        Student owner = new Student(); owner.setId(ownerId);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(attemptId);
        attempt.setStatus(QuizAttemptStatus.STARTED);
        attempt.setStudent(owner);

        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        
        RuntimeException ex = assertThrows(RuntimeException.class, () -> 
            quizAttemptServices.completeQuizAttempt(attemptId, hackerId)
        );
        assertTrue(ex.getMessage().contains("not your attempt"));
    }
}