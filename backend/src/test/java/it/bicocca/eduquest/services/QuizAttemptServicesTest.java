package it.bicocca.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import it.bicocca.eduquest.domain.answers.*;
import it.bicocca.eduquest.domain.quiz.*;
import it.bicocca.eduquest.domain.users.*;
import it.bicocca.eduquest.dto.quiz.QuestionType;
import it.bicocca.eduquest.dto.quizAttempt.AnswerDTO;
import it.bicocca.eduquest.dto.quizAttempt.QuizAttemptDTO;
import it.bicocca.eduquest.dto.quizAttempt.QuizSessionDTO;
import it.bicocca.eduquest.repository.*;

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
    void testStartQuizSuccess() {
        long quizId = 1L;
        long studentId = 2L;

        Student student = new Student();
        student.setId(studentId);

        Teacher author = new Teacher();
        author.setId(99L);

        Quiz quiz = new Quiz("Title", "Desc", author);
        quiz.setId(quizId);
        
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
        when(quizAttemptsRepository.findByStudentAndStatus(student, QuizAttemptStatus.STARTED))
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
    void testStartQuizResumeAttempt() {
        long quizId = 1L;
        long studentId = 2L;
        Student student = new Student();
        Quiz quiz = new Quiz("Title", "Desc", new Teacher());
        quiz.setId(quizId);
        
        QuizAttempt existingAttempt = new QuizAttempt(student, quiz);
        existingAttempt.setId(500L);
        
        OpenQuestion q = new OpenQuestion(); 
        q.setId(10L); q.setAuthor(new Teacher()); q.setQuestionType(QuestionType.OPENED);
        OpenAnswer existingAns = new OpenAnswer(existingAttempt, q, "Old Answer");
        existingAttempt.addAnswer(existingAns);

        when(usersRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(quizAttemptsRepository.findByStudentAndStatus(student, QuizAttemptStatus.STARTED))
                .thenReturn(Optional.of(existingAttempt));

        QuizSessionDTO result = quizAttemptServices.startQuiz(quizId, studentId);

        assertEquals(500L, result.getAttemptId());
        assertEquals(1, result.getExistingAnswers().size()); 
        verify(quizAttemptsRepository, never()).save(any(QuizAttempt.class));
    }

    @Test
    void testStartQuizFailures() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> quizAttemptServices.startQuiz(1L, 1L));

        Teacher teacher = new Teacher();
        when(usersRepository.findById(2L)).thenReturn(Optional.of(teacher));
        RuntimeException ex2 = assertThrows(RuntimeException.class, () -> quizAttemptServices.startQuiz(1L, 2L));
        assertTrue(ex2.getMessage().contains("not a Student"));

        Student student = new Student();
        when(usersRepository.findById(3L)).thenReturn(Optional.of(student));
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> quizAttemptServices.startQuiz(99L, 3L));
    }

    @Test
    void testSaveSingleAnswerCreateNew() {
        long attemptId = 1L;
        long questionId = 10L;
        long userId = 50L; 

        Student student = new Student(); 
        student.setId(userId); 

        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(attemptId);
        attempt.setStatus(QuizAttemptStatus.STARTED);
        attempt.setStudent(student); 

        OpenQuestion question = new OpenQuestion();
        question.setId(questionId);
        question.setQuestionType(QuestionType.OPENED);

        AnswerDTO dto = new AnswerDTO();
        dto.setQuizAttemptId(attemptId);
        dto.setQuestionId(questionId);
        dto.setTextOpenAnswer("Risposta aperta");

        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(questionsRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(answersRepository.findByQuizAttemptAndQuestion(attempt, question)).thenReturn(Optional.empty());
        
        when(answersRepository.save(any(Answer.class))).thenAnswer(i -> i.getArgument(0));

        AnswerDTO result = quizAttemptServices.saveSingleAnswer(dto, userId);

        assertEquals("Risposta aperta", result.getTextOpenAnswer());
        verify(answersRepository).save(any(OpenAnswer.class));
    }

    @Test
    void testSaveSingleAnswerUpdateExisting() {
        long attemptId = 1L;
        long questionId = 20L;
        long optionId = 100L;
        long userId = 50L;

        Student student = new Student(); 
        student.setId(userId);

        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(attemptId);
        attempt.setStatus(QuizAttemptStatus.STARTED);
        attempt.setStudent(student); 

        ClosedQuestion question = new ClosedQuestion();
        question.setId(questionId); 
        question.setQuestionType(QuestionType.CLOSED);
        
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

        AnswerDTO result = quizAttemptServices.saveSingleAnswer(dto, userId);

        assertEquals(optionId, result.getSelectedOptionId());
        verify(answersRepository).save(existingAnswer);
    }
    
    @Test
    void testSaveSingleAnswerNotYourAttempt() {
        long attemptId = 1L;
        long ownerId = 10L;
        long hackerId = 666L;

        Student owner = new Student(); owner.setId(ownerId);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(attemptId);
        attempt.setStatus(QuizAttemptStatus.STARTED);
        attempt.setStudent(owner); 

        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        AnswerDTO dto = new AnswerDTO();
        dto.setQuizAttemptId(attemptId);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> 
            quizAttemptServices.saveSingleAnswer(dto, hackerId)
        );
        assertTrue(ex.getMessage().contains("not your attempt"));
    }

    @Test
    void testSaveSingleAnswerFailures() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);
        
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(1L);
        attempt.setStudent(student); 

        when(quizAttemptsRepository.findById(1L)).thenReturn(Optional.of(attempt));

        // Test 1: Quiz Completed
        attempt.setStatus(QuizAttemptStatus.COMPLETED);
        AnswerDTO dto = new AnswerDTO(); dto.setQuizAttemptId(1L);
        assertThrows(RuntimeException.class, () -> quizAttemptServices.saveSingleAnswer(dto, userId));
    }
    
    @Test
    void testSaveSingleAnswerInconsistency() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(1L);
        attempt.setStatus(QuizAttemptStatus.STARTED);
        attempt.setStudent(student); 

        when(quizAttemptsRepository.findById(1L)).thenReturn(Optional.of(attempt));
        
        ClosedQuestion closedQ = new ClosedQuestion(); 
        closedQ.setId(2L);
        closedQ.setQuestionType(QuestionType.CLOSED);
        when(questionsRepository.findById(2L)).thenReturn(Optional.of(closedQ));
        
        OpenAnswer wrongTypeAnswer = new OpenAnswer();
        when(answersRepository.findByQuizAttemptAndQuestion(attempt, closedQ)).thenReturn(Optional.of(wrongTypeAnswer));
        
        AnswerDTO dto = new AnswerDTO();
        dto.setQuizAttemptId(1L);
        dto.setQuestionId(2L);
        dto.setSelectedOptionId(5L);
        
        RuntimeException ex = assertThrows(RuntimeException.class, () -> quizAttemptServices.saveSingleAnswer(dto, userId));
        assertTrue(ex.getMessage().contains("inconsistency"));
    }

    @Test
    void testSaveSingleAnswerInvalidOption() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);

        QuizAttempt attempt = new QuizAttempt(); 
        attempt.setId(1L);
        attempt.setStatus(QuizAttemptStatus.STARTED);
        attempt.setStudent(student);

        ClosedQuestion question = new ClosedQuestion(); 
        question.setId(2L);
        question.setQuestionType(QuestionType.CLOSED);
        
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
    void testCompleteQuizAttemptScoring() {
        long attemptId = 1L;
        
        long q1Id = 101L; 
        long q2Id = 102L;
        long q3Id = 103L;
        
        Student student = new Student(); 
        student.setId(10L); 
        student.setName("Mario"); 
        student.setSurname("Rossi");

        Quiz quiz = new Quiz(); 
        quiz.setId(20L); 
        quiz.setTitle("Final Quiz");
        
        QuizAttempt attempt = new QuizAttempt(student, quiz);
        attempt.setId(attemptId);
        attempt.setStatus(QuizAttemptStatus.STARTED);

        ClosedQuestion q1 = new ClosedQuestion();
        q1.setId(q1Id);
        q1.setQuestionType(QuestionType.CLOSED);
        ClosedQuestionOption optCorrect = new ClosedQuestionOption("A", true);
        ClosedAnswer a1 = new ClosedAnswer(attempt, q1, optCorrect);
        
        OpenQuestion q2 = new OpenQuestion();
        q2.setId(q2Id);
        q2.setQuestionType(QuestionType.OPENED);
        OpenQuestionAcceptedAnswer valid = new OpenQuestionAcceptedAnswer("Paris");
        q2.addAnswer(valid);
        OpenAnswer a2 = new OpenAnswer(attempt, q2, " paris "); 

        OpenQuestion q3 = new OpenQuestion();
        q3.setId(q3Id);
        q3.setQuestionType(QuestionType.OPENED);
        OpenQuestionAcceptedAnswer validQ3 = new OpenQuestionAcceptedAnswer("Rome");
        q3.addAnswer(validQ3); 
        OpenAnswer a3 = new OpenAnswer(attempt, q3, "Berlin");
        
        quiz.setQuestions(Arrays.asList(q1, q2, q3));
        attempt.setAnswers(Arrays.asList(a1, a2, a3));

        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        
        lenient().when(questionsRepository.findById(q1Id)).thenReturn(Optional.of(q1));
        lenient().when(questionsRepository.findById(q2Id)).thenReturn(Optional.of(q2));
        lenient().when(questionsRepository.findById(q3Id)).thenReturn(Optional.of(q3));
        
        QuizAttemptDTO result = quizAttemptServices.completeQuizAttempt(attemptId, 10L);

        assertEquals(QuizAttemptStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getFinishedAt());
        assertEquals(3, result.getMaxScore());
        assertEquals(2, result.getScore());
    }
    
    @Test
    void testCompleteQuizAttemptNotYourAttempt() {
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
    
    @Test
    void testGetQuizAttemptByIdSuccess() {
        long attemptId = 1L;
        long userId = 11L;

        Student student = new Student(); student.setId(userId); student.setName("Lucky"); student.setSurname("Luke");
        Quiz quiz = new Quiz(); quiz.setId(5L); quiz.setTitle("Quiz");
        
        QuizAttempt attempt = new QuizAttempt(student, quiz);
        attempt.setId(attemptId);
        attempt.setStartedAt(LocalDateTime.now()); 

        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        QuizAttemptDTO result = quizAttemptServices.getQuizAttemptById(attemptId, userId);

        assertNotNull(result);
        assertEquals(attemptId, result.getId());
    }

    @Test
    void testGetQuizAttemptByIdNotAuthorized() {
        long attemptId = 1L;
        long ownerId = 11L;
        long hackerId = 111L;

        Student owner = new Student(); owner.setId(ownerId);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(owner);

        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> 
            quizAttemptServices.getQuizAttemptById(attemptId, hackerId));
        assertEquals("Not authorized", ex.getMessage());
    }
    
    @Test
    void testGetQuizAttemptByIdNotFound() {
        when(quizAttemptsRepository.findById(11L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> quizAttemptServices.getQuizAttemptById(11L, 1L));
    }

    @Test
    void testGetQuizAttemptsByUserIdSuccess() {
        long userId = 11L;
        Student student = new Student(); student.setId(userId); student.setName("N"); student.setSurname("S");
        Quiz quiz = new Quiz(); quiz.setId(1L); quiz.setTitle("T");

        QuizAttempt attempt = new QuizAttempt(student, quiz);
        attempt.setStartedAt(LocalDateTime.now());
        
        when(quizAttemptsRepository.findByStudentId(userId)).thenReturn(Arrays.asList(attempt));

        List<QuizAttemptDTO> results = quizAttemptServices.getQuizAttemptsByUserId(userId);

        assertEquals(1, results.size()); 
        assertEquals(userId, results.get(0).getStudentId());
    }
    
    @Mock private TestRepository testRepository;
    @org.junit.jupiter.api.Test
    void startQuizAttemptsBelowLimit() {
        long quizId = 1L;
        long userId = 2L;
        long testId = 100L;

        Student student = new Student(); student.setId(userId);
        Quiz quiz = new Quiz(); quiz.setId(quizId);
        
        it.bicocca.eduquest.domain.quiz.Test testEntity = new it.bicocca.eduquest.domain.quiz.Test();
        testEntity.setId(testId);
        testEntity.setMaxTries(3); 
        testEntity.setQuiz(quiz);

        when(usersRepository.findById(userId)).thenReturn(Optional.of(student));
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(quizAttemptsRepository.findByStudentAndStatus(any(), any())).thenReturn(Optional.empty());
        
        when(testRepository.findById(testId)).thenReturn(Optional.of(testEntity));
        when(quizAttemptsRepository.countByStudentAndTest(student, testEntity)).thenReturn(1L); 
        
        when(quizAttemptsRepository.save(any(QuizAttempt.class))).thenAnswer(i -> {
            QuizAttempt q = i.getArgument(0);
            q.setId(55L);
            return q;
        });

        QuizSessionDTO result = quizAttemptServices.startQuiz(quizId, userId, testId);

        assertNotNull(result);
        assertEquals(55L, result.getAttemptId());
        verify(testRepository).findById(testId);
    }

    @org.junit.jupiter.api.Test
    void startQuizMaxAttemptsReached() {
        long quizId = 1L;
        long userId = 2L;
        long testId = 100L;

        Student student = new Student(); student.setId(userId);
        Quiz quiz = new Quiz(); quiz.setId(quizId);
        
        it.bicocca.eduquest.domain.quiz.Test testEntity = new it.bicocca.eduquest.domain.quiz.Test();
        testEntity.setId(testId);
        testEntity.setMaxTries(2); 
        testEntity.setQuiz(quiz);

        when(usersRepository.findById(userId)).thenReturn(Optional.of(student));
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        lenient().when(quizAttemptsRepository.findByStudentAndStatus(any(), any())).thenReturn(Optional.empty());
        
        when(testRepository.findById(testId)).thenReturn(Optional.of(testEntity));
        when(quizAttemptsRepository.countByStudentAndTest(student, testEntity)).thenReturn(2L);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> 
            quizAttemptServices.startQuiz(quizId, userId, testId)
        );
        assertTrue(ex.getMessage().contains("Max attempts reached"));
    }
    
    @org.junit.jupiter.api.Test
    void startQuizNotFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(new Student()));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(new Quiz()));
        when(quizAttemptsRepository.findByStudentAndStatus(any(), any())).thenReturn(Optional.empty());
        
        when(testRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            quizAttemptServices.startQuiz(1L, 1L, 999L)
        );
    }
    
    @Test
    void getQuizSessionMultimediaMapping() {
        long attemptId = 50L;
        long quizId = 10L;
        
        Teacher author = new Teacher();
        author.setId(99L); 
        
        Quiz quiz = new Quiz("Title", "Desc", author);
        quiz.setId(quizId);
        
        OpenQuestion qVideo = new OpenQuestion();
        qVideo.setId(1L);
        qVideo.setText("Watch this");
        qVideo.setQuestionType(QuestionType.OPENED);
        qVideo.setAuthor(author); 
        
        it.bicocca.eduquest.domain.multimedia.VideoSupport video = mock(it.bicocca.eduquest.domain.multimedia.VideoSupport.class);
        when(video.getUrl()).thenReturn("yt_link");
        when(video.getIsYoutube()).thenReturn(true);
        when(video.getType()).thenReturn(it.bicocca.eduquest.domain.multimedia.MultimediaType.VIDEO);
        qVideo.setMultimedia(video);
        
        OpenQuestion qImage = new OpenQuestion();
        qImage.setId(2L);
        qImage.setText("Look at this");
        qImage.setQuestionType(QuestionType.OPENED);
        qImage.setAuthor(author); 
        
        it.bicocca.eduquest.domain.multimedia.ImageSupport image = mock(it.bicocca.eduquest.domain.multimedia.ImageSupport.class);
        when(image.getUrl()).thenReturn("img_link");
        when(image.getType()).thenReturn(it.bicocca.eduquest.domain.multimedia.MultimediaType.IMAGE);
        qImage.setMultimedia(image); 

        quiz.setQuestions(Arrays.asList(qVideo, qImage));

        QuizAttempt attempt = new QuizAttempt(new Student(), quiz);
        attempt.setId(attemptId);
        attempt.addAnswer(new OpenAnswer(attempt, qVideo, "Seen it"));

        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        QuizSessionDTO result = quizAttemptServices.getQuizSession(attemptId);

        assertNotNull(result);
        assertEquals(attemptId, result.getAttemptId());
        
        var media1 = result.getQuestions().get(0).getMultimedia();
        assertEquals("yt_link", media1.getUrl());
        assertTrue(media1.getIsYoutube());
        
        var media2 = result.getQuestions().get(1).getMultimedia();
        assertEquals("img_link", media2.getUrl());
        assertFalse(media2.getIsYoutube()); 
    }

    @Test
    void getQuizSessionNotFound() {
        when(quizAttemptsRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizAttemptServices.getQuizSession(99L));
    }
    
    @Test
    void getQuizSessionThrow() {
        Quiz quiz = new Quiz();
        quiz.setId(10L);
        
        Question weirdQ = new Question() {};
        weirdQ.setId(99L);
        weirdQ.setText("Alien Question");
        
        quiz.setQuestions(List.of(weirdQ));
        
        QuizAttempt attempt = new QuizAttempt(new Student(), quiz);
        attempt.setId(50L);

        when(quizAttemptsRepository.findById(50L)).thenReturn(Optional.of(attempt));
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> 
            quizAttemptServices.getQuizSession(50L)
        );
        assertEquals("Not supported question type.", e.getMessage());
    }
}