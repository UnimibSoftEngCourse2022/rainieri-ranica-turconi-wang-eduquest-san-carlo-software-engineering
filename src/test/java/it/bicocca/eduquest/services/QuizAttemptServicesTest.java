package it.bicocca.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Mock private TestRepository testRepository;
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
        existingAttempt.setStatus(QuizAttemptStatus.STARTED);
        
        OpenQuestion q = new OpenQuestion(); 
        q.setId(10L); q.setAuthor(new Teacher()); q.setQuestionType(QuestionType.OPENED);
        OpenAnswer existingAns = new OpenAnswer(existingAttempt, q, "Old Answer");
        existingAttempt.addAnswer(existingAns);

        when(usersRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(quizAttemptsRepository.findByStudentAndStatus(student, QuizAttemptStatus.STARTED))
                .thenReturn(Optional.of(existingAttempt));

        QuizSessionDTO result = quizAttemptServices.startQuiz(quizId, studentId);

        assertEquals(500L, result.getAttemptId());
        assertTrue(result.isResumed());
        assertEquals(1, result.getExistingAnswers().size()); 
        verify(quizAttemptsRepository, never()).save(any(QuizAttempt.class));
    }
    
    @Test
    void testStartQuiz_DifferentQuizInProgress() {
        long requestedQuizId = 1L;
        long existingQuizId = 2L;
        long studentId = 10L;
        
        Student student = new Student();
        Quiz existingQuiz = new Quiz(); existingQuiz.setId(existingQuizId); existingQuiz.setTitle("Old Quiz");
        QuizAttempt existingAttempt = new QuizAttempt(student, existingQuiz);
        existingAttempt.setStatus(QuizAttemptStatus.STARTED);

        when(usersRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(quizAttemptsRepository.findByStudentAndStatus(student, QuizAttemptStatus.STARTED))
                .thenReturn(Optional.of(existingAttempt));

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> 
            quizAttemptServices.startQuiz(requestedQuizId, studentId)
        );
        assertTrue(e.getMessage().contains("already have a quiz in progress"));
    }

    @Test
    void testStartQuiz_ContextMismatch_PracticeVsTest() {
        long quizId = 1L;
        long studentId = 10L;
        
        Student student = new Student();
        Quiz quiz = new Quiz(); quiz.setId(quizId);
        
        QuizAttempt existingAttempt = new QuizAttempt(student, quiz);
        existingAttempt.setTest(null); 
        existingAttempt.setStatus(QuizAttemptStatus.STARTED);

        when(usersRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(quizAttemptsRepository.findByStudentAndStatus(student, QuizAttemptStatus.STARTED))
                .thenReturn(Optional.of(existingAttempt));

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> 
            quizAttemptServices.startQuiz(quizId, studentId, 100L)
        );
        assertTrue(e.getMessage().contains("finish it first"));
    }
    
    @Test
    void testStartQuiz_ContextMismatch_TestVsPractice() {
        long quizId = 1L;
        long studentId = 10L;
        
        Student student = new Student();
        Quiz quiz = new Quiz(); quiz.setId(quizId);
        
        it.bicocca.eduquest.domain.quiz.Test test = new it.bicocca.eduquest.domain.quiz.Test();
        test.setId(200L);
        QuizAttempt existingAttempt = new QuizAttempt(student, quiz);
        existingAttempt.setTest(test);
        existingAttempt.setStatus(QuizAttemptStatus.STARTED);

        when(usersRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(quizAttemptsRepository.findByStudentAndStatus(student, QuizAttemptStatus.STARTED))
                .thenReturn(Optional.of(existingAttempt));

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> 
            quizAttemptServices.startQuiz(quizId, studentId, null)
        );
        assertTrue(e.getMessage().contains("finish it first"));
    }

    @Test
    void testStartQuiz_TestNotFound() {
        long quizId = 1L;
        long studentId = 10L;
        long testId = 999L;
        
        Student student = new Student();
        when(usersRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(new Quiz()));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> 
            quizAttemptServices.startQuiz(quizId, studentId, testId)
        );
        assertTrue(e.getMessage().contains("Test not found"));
    }

    @Test
    void testStartQuizFailures_UserNotFound_Or_NotStudent() {
        assertThrows(IllegalArgumentException.class, () -> quizAttemptServices.startQuiz(1L, 1L));

        Teacher teacher = new Teacher();
        when(usersRepository.findById(2L)).thenReturn(Optional.of(teacher));
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> quizAttemptServices.startQuiz(1L, 2L));
        assertTrue(ex2.getMessage().contains("not a Student"));
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
    void testSaveSingleAnswer_TimeLimitExceeded() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);
        
        it.bicocca.eduquest.domain.quiz.Test test = new it.bicocca.eduquest.domain.quiz.Test();
        test.setMaxDuration(Duration.ofMinutes(10)); 
        
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(1L);
        attempt.setStudent(student);
        attempt.setStatus(QuizAttemptStatus.STARTED);
        attempt.setTest(test);
        attempt.setStartedAt(LocalDateTime.now().minusMinutes(20));

        when(quizAttemptsRepository.findById(1L)).thenReturn(Optional.of(attempt));

        AnswerDTO dto = new AnswerDTO(); dto.setQuizAttemptId(1L);
        
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> 
            quizAttemptServices.saveSingleAnswer(dto, userId)
        );
        assertTrue(e.getMessage().contains("Time limit exceeded"));
    }

    @Test
    void testSaveSingleAnswer_QuizNotStarted() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(1L);
        attempt.setStudent(student);
        attempt.setStatus(QuizAttemptStatus.COMPLETED);

        when(quizAttemptsRepository.findById(1L)).thenReturn(Optional.of(attempt));

        AnswerDTO dto = new AnswerDTO(); dto.setQuizAttemptId(1L);
        
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> 
            quizAttemptServices.saveSingleAnswer(dto, userId)
        );
        assertTrue(e.getMessage().contains("already been submitted"));
    }
    
    @Test
    void testSaveSingleAnswer_TypeInconsistency_ClosedQ_OpenA() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(1L);
        attempt.setStatus(QuizAttemptStatus.STARTED);
        attempt.setStudent(student); 

        ClosedQuestion closedQ = new ClosedQuestion(); 
        closedQ.setId(2L);
        closedQ.setQuestionType(QuestionType.CLOSED);
        
        OpenAnswer wrongTypeAnswer = new OpenAnswer();
        
        when(quizAttemptsRepository.findById(1L)).thenReturn(Optional.of(attempt));
        when(questionsRepository.findById(2L)).thenReturn(Optional.of(closedQ));
        when(answersRepository.findByQuizAttemptAndQuestion(attempt, closedQ)).thenReturn(Optional.of(wrongTypeAnswer));
        
        AnswerDTO dto = new AnswerDTO();
        dto.setQuizAttemptId(1L);
        dto.setQuestionId(2L);
        
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> 
            quizAttemptServices.saveSingleAnswer(dto, userId)
        );
        assertTrue(ex.getMessage().contains("inconsistency"));
    }

    @Test
    void testSaveSingleAnswer_ClosedQuestion_OptionNull() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(1L); attempt.setStatus(QuizAttemptStatus.STARTED); attempt.setStudent(student);

        ClosedQuestion question = new ClosedQuestion(); 
        question.setId(2L); question.setQuestionType(QuestionType.CLOSED);
        ClosedAnswer answer = new ClosedAnswer();

        when(quizAttemptsRepository.findById(1L)).thenReturn(Optional.of(attempt));
        when(questionsRepository.findById(2L)).thenReturn(Optional.of(question));
        when(answersRepository.findByQuizAttemptAndQuestion(attempt, question)).thenReturn(Optional.of(answer));

        AnswerDTO dto = new AnswerDTO();
        dto.setQuizAttemptId(1L); dto.setQuestionId(2L);
        dto.setSelectedOptionId(null); 

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
            quizAttemptServices.saveSingleAnswer(dto, userId)
        );
        assertTrue(ex.getMessage().contains("must select an option"));
    }
    
    @Test
    void testSaveSingleAnswer_ClosedQuestion_OptionNotFound() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(1L); attempt.setStatus(QuizAttemptStatus.STARTED); attempt.setStudent(student);

        ClosedQuestion question = new ClosedQuestion(); 
        question.setId(2L); question.setQuestionType(QuestionType.CLOSED);
        ClosedQuestionOption opt1 = new ClosedQuestionOption("A", true); opt1.setId(10L);
        question.addOption(opt1);
        
        ClosedAnswer answer = new ClosedAnswer();

        when(quizAttemptsRepository.findById(1L)).thenReturn(Optional.of(attempt));
        when(questionsRepository.findById(2L)).thenReturn(Optional.of(question));
        when(answersRepository.findByQuizAttemptAndQuestion(attempt, question)).thenReturn(Optional.of(answer));

        AnswerDTO dto = new AnswerDTO();
        dto.setQuizAttemptId(1L); dto.setQuestionId(2L);
        dto.setSelectedOptionId(999L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
            quizAttemptServices.saveSingleAnswer(dto, userId)
        );
        assertTrue(ex.getMessage().contains("does not belong to this question"));
    }

    @Test
    void testSaveSingleAnswer_UnsupportedQuestionType() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(1L); attempt.setStatus(QuizAttemptStatus.STARTED); attempt.setStudent(student);
        
        Question weirdQuestion = mock(Question.class);
        
        when(quizAttemptsRepository.findById(1L)).thenReturn(Optional.of(attempt));
        when(questionsRepository.findById(2L)).thenReturn(Optional.of(weirdQuestion));
        
        AnswerDTO dto = new AnswerDTO();
        dto.setQuizAttemptId(1L); dto.setQuestionId(2L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
            quizAttemptServices.saveSingleAnswer(dto, userId)
        );
        assertEquals("Not supported question type.", ex.getMessage());
    }

    @Test
    void testCompleteQuizAttemptScoring() {
        long attemptId = 1L;
        
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

        ClosedQuestion q1 = new ClosedQuestion(); q1.setId(101L); q1.setQuestionType(QuestionType.CLOSED);
        ClosedQuestionOption optCorrect = new ClosedQuestionOption("A", true);
        ClosedAnswer a1 = new ClosedAnswer(attempt, q1, optCorrect);
        
        OpenQuestion q2 = new OpenQuestion(); q2.setId(102L); q2.setQuestionType(QuestionType.OPENED);
        OpenQuestionAcceptedAnswer valid = new OpenQuestionAcceptedAnswer("Paris");
        q2.addAnswer(valid);
        OpenAnswer a2 = new OpenAnswer(attempt, q2, " paris "); 
        
        OpenQuestion q3 = new OpenQuestion(); q3.setId(103L);
        
        OpenQuestion q4 = new OpenQuestion(); q4.setId(104L); q4.setQuestionType(QuestionType.OPENED);
        OpenQuestionAcceptedAnswer valid4 = new OpenQuestionAcceptedAnswer("Rome");
        q4.addAnswer(valid4);
        OpenAnswer a4 = new OpenAnswer(attempt, q4, "Milan");

        quiz.setQuestions(Arrays.asList(q1, q2, q3, q4));
        attempt.setAnswers(Arrays.asList(a1, a2, a4));

        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        
        lenient().when(questionsRepository.findById(101L)).thenReturn(Optional.of(q1));
        lenient().when(questionsRepository.findById(102L)).thenReturn(Optional.of(q2));
        lenient().when(questionsRepository.findById(104L)).thenReturn(Optional.of(q4));
        
        QuizAttemptDTO result = quizAttemptServices.completeQuizAttempt(attemptId, 10L);

        assertEquals(QuizAttemptStatus.COMPLETED, result.getStatus());
        assertEquals(4, result.getMaxScore());
        assertEquals(2, result.getScore());
    }
    
    @Test
    void testCompleteQuizAttempt_UnsupportedQuestionType_Score() {
        long attemptId = 1L;
        Student student = new Student(); student.setId(10L);
        Quiz quiz = new Quiz();
        QuizAttempt attempt = new QuizAttempt(student, quiz);
        attempt.setId(attemptId);
        attempt.setStatus(QuizAttemptStatus.STARTED);
        
        Question weirdQ = mock(Question.class);
        when(weirdQ.getId()).thenReturn(100L);
        quiz.setQuestions(List.of(weirdQ));
        
        Answer weirdA = mock(Answer.class);
        when(weirdA.getQuestion()).thenReturn(weirdQ);
        attempt.setAnswers(List.of(weirdA));
        
        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
            quizAttemptServices.completeQuizAttempt(attemptId, 10L)
        );
        assertEquals("Not supported question type.", ex.getMessage());
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
    void testGetQuizAttemptsByUserId_FilterCheck() {
        long userId = 11L;
        long otherUserId = 22L;
        
        Student student = new Student(); student.setId(userId);
        Student otherStudent = new Student(); otherStudent.setId(otherUserId);
        
        Quiz quiz = new Quiz(); quiz.setId(1L); quiz.setTitle("T");

        QuizAttempt attempt1 = new QuizAttempt(student, quiz);
        QuizAttempt attempt2 = new QuizAttempt(otherStudent, quiz); 
        
        when(quizAttemptsRepository.findByStudentId(userId)).thenReturn(Arrays.asList(attempt1, attempt2));

        List<QuizAttemptDTO> results = quizAttemptServices.getQuizAttemptsByUserId(userId);

        assertEquals(1, results.size()); 
        assertEquals(userId, results.get(0).getStudentId());
    }
    
    @Test
    void testStartQuizMaxAttemptsReached() {
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
        
        when(testRepository.findById(testId)).thenReturn(Optional.of(testEntity));
        when(quizAttemptsRepository.countByStudentAndTest(student, testEntity)).thenReturn(2L);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> 
            quizAttemptServices.startQuiz(quizId, userId, testId)
        );
        assertTrue(ex.getMessage().contains("Max attempts reached"));
    }
    
    @Test
    void testGetQuizSessionMultimediaMapping() {
        long attemptId = 50L;
        long quizId = 10L;
        
        Teacher author = new Teacher();
        author.setId(99L); 
        
        Quiz quiz = new Quiz("Title", "Desc", author);
        quiz.setId(quizId);
        
        OpenQuestion qVideo = new OpenQuestion();
        qVideo.setId(1L);
        qVideo.setQuestionType(QuestionType.OPENED);
        qVideo.setAuthor(author); 
        
        it.bicocca.eduquest.domain.multimedia.VideoSupport video = mock(it.bicocca.eduquest.domain.multimedia.VideoSupport.class);
        when(video.getUrl()).thenReturn("yt_link");
        when(video.getIsYoutube()).thenReturn(true);
        when(video.getType()).thenReturn(it.bicocca.eduquest.domain.multimedia.MultimediaType.VIDEO);
        qVideo.setMultimedia(video);
        
        OpenQuestion qImage = new OpenQuestion();
        qImage.setId(2L);
        qImage.setQuestionType(QuestionType.OPENED);
        qImage.setAuthor(author); 
        
        it.bicocca.eduquest.domain.multimedia.ImageSupport image = mock(it.bicocca.eduquest.domain.multimedia.ImageSupport.class);
        when(image.getUrl()).thenReturn("img_link");
        when(image.getType()).thenReturn(it.bicocca.eduquest.domain.multimedia.MultimediaType.IMAGE);
        qImage.setMultimedia(image); 

        quiz.setQuestions(Arrays.asList(qVideo, qImage));

        QuizAttempt attempt = new QuizAttempt(new Student(), quiz);
        attempt.setId(attemptId);

        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        QuizSessionDTO result = quizAttemptServices.getQuizSession(attemptId);

        assertNotNull(result);
        var media1 = result.getQuestions().get(0).getMultimedia();
        assertEquals("yt_link", media1.getUrl());
        assertTrue(media1.getIsYoutube());
        
        var media2 = result.getQuestions().get(1).getMultimedia();
        assertEquals("img_link", media2.getUrl());
        assertFalse(media2.getIsYoutube()); 
    }

    @Test
    void testGetQuizSession_QuizNotFoundInRepo() {
        long attemptId = 50L;
        long quizId = 10L;
        Quiz quiz = new Quiz(); quiz.setId(quizId);
        QuizAttempt attempt = new QuizAttempt(new Student(), quiz);
        attempt.setId(attemptId);
        
        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        
        assertThrows(IllegalArgumentException.class, () -> quizAttemptServices.getQuizSession(attemptId));
    }

    @Test
    void testGetQuizSession_UnsupportedQuestion() {
        Quiz quiz = new Quiz(); quiz.setId(10L);
        Question weirdQ = mock(Question.class);
        when(weirdQ.getStats()).thenReturn(new QuestionStats()); 
        
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
    
    @Test
    void testConvertAnswerToDTO_UnsupportedType() {
        long attemptId = 1L;
        Quiz quiz = new Quiz(); quiz.setId(10L); quiz.setQuestions(new ArrayList<>());
        QuizAttempt attempt = new QuizAttempt(new Student(), quiz);
        attempt.setId(attemptId);
        
        Answer weirdAnswer = mock(Answer.class); 
        when(weirdAnswer.getId()).thenReturn(5L);
        when(weirdAnswer.getQuizAttempt()).thenReturn(attempt);
        
        Question q = new OpenQuestion();
        q.setId(100L);
        when(weirdAnswer.getQuestion()).thenReturn(q);
        
        attempt.setAnswers(List.of(weirdAnswer));

        when(quizAttemptsRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> 
            quizAttemptServices.getQuizSession(attemptId)
        );
        assertEquals("Not supported answer type.", e.getMessage());
    }
}