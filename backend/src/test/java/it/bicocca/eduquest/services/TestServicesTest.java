package it.bicocca.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.domain.users.Teacher;
import it.bicocca.eduquest.dto.quiz.QuizDTO;
import it.bicocca.eduquest.dto.quiz.TestAddDTO;
import it.bicocca.eduquest.dto.quiz.TestDTO;
import it.bicocca.eduquest.repository.QuizAttemptsRepository;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.repository.TestRepository;
import it.bicocca.eduquest.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
class TestServicesTest {

    @Mock private TestRepository testRepository;
    @Mock private QuizRepository quizRepository;
    @Mock private UsersRepository usersRepository;
    @Mock private QuizServices quizServices;
    @Mock private QuizAttemptsRepository quizAttemptsRepository;

    @InjectMocks
    private TestServices testServices;

    private Teacher teacher;
    private Quiz quiz;
    private it.bicocca.eduquest.domain.quiz.Test testEntity;
    private QuizDTO quizDTO;

    @BeforeEach
    void setUp() {
        teacher = new Teacher();
        teacher.setId(1L);

        quiz = new Quiz();
        quiz.setId(10L);
        quiz.setAuthor(teacher);

        testEntity = new it.bicocca.eduquest.domain.quiz.Test();
        testEntity.setId(100L);
        testEntity.setQuiz(quiz);
        testEntity.setMaxDuration(Duration.ofMinutes(60));
        testEntity.setMaxTries(3);

        quizDTO = new QuizDTO();
        quizDTO.setId(10L);
    }

    private void mockStats(it.bicocca.eduquest.domain.quiz.Test t) {
        lenient().when(quizAttemptsRepository.getAverageScoreByTest(t)).thenReturn(8.5);
        lenient().when(quizAttemptsRepository.countByTest(t)).thenReturn(5L);
        lenient().when(quizServices.getQuizById(t.getQuiz().getId())).thenReturn(quizDTO);
    }

    @Test
    void testCreateTest_Success() {
        TestAddDTO dto = new TestAddDTO();
        dto.setQuizId(10L);
        dto.setMaxDurationMinutes(60);
        dto.setMaxTries(3);

        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        
        when(testRepository.save(any(it.bicocca.eduquest.domain.quiz.Test.class))).thenAnswer(i -> {
            it.bicocca.eduquest.domain.quiz.Test t = i.getArgument(0);
            t.setId(100L);
            return t;
        });
        
        mockStats(testEntity);
        
        when(quizServices.getQuizById(10L)).thenReturn(quizDTO);
        when(quizAttemptsRepository.getAverageScoreByTest(any())).thenReturn(0.0);
        when(quizAttemptsRepository.countByTest(any())).thenReturn(0L);

        TestDTO result = testServices.createTest(dto, 1L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(60L, result.getMaxDuration());
        assertEquals(3, result.getMaxTries());
    }

    @Test
    void testCreateTest_UserNotFound() {
        TestAddDTO dto = new TestAddDTO();
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> testServices.createTest(dto, 1L));
    }

    @Test
    void testCreateTest_NotTeacher() {
        Student student = new Student();
        student.setId(2L);
        when(usersRepository.findById(2L)).thenReturn(Optional.of(student));
        
        TestAddDTO dto = new TestAddDTO();
        assertThrows(IllegalArgumentException.class, () -> testServices.createTest(dto, 2L));
    }

    @Test
    void testCreateTest_QuizNotFound() {
        TestAddDTO dto = new TestAddDTO();
        dto.setQuizId(99L);
        
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> testServices.createTest(dto, 1L));
    }

    @Test
    void testCreateTest_NotOwner() {
        Teacher otherTeacher = new Teacher();
        otherTeacher.setId(5L);
        
        TestAddDTO dto = new TestAddDTO();
        dto.setQuizId(10L);

        when(usersRepository.findById(5L)).thenReturn(Optional.of(otherTeacher));
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));

        assertThrows(IllegalStateException.class, () -> testServices.createTest(dto, 5L));
    }

    @Test
    void testGetTestById_Success() {
        when(testRepository.findById(100L)).thenReturn(Optional.of(testEntity));
        mockStats(testEntity);

        TestDTO result = testServices.getTestById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(8.5, result.getTestAverageScore());
        assertEquals(5, result.getTestTotalAttempts());
    }

    @Test
    void testGetTestById_NotFound() {
        when(testRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> testServices.getTestById(999L));
    }

    @Test
    void testGetAllTests() {
        it.bicocca.eduquest.domain.quiz.Test t2 = new it.bicocca.eduquest.domain.quiz.Test();
        t2.setId(101L);
        t2.setQuiz(quiz);
        
        when(testRepository.findAll()).thenReturn(Arrays.asList(testEntity, t2));
        mockStats(testEntity);
        mockStats(t2);

        List<TestDTO> result = testServices.getAllTests();

        assertEquals(2, result.size());
    }

    @Test
    void testGetTestsByTeacherId() {
        it.bicocca.eduquest.domain.quiz.Test t1 = testEntity; 
        
        Teacher otherTeacher = new Teacher(); otherTeacher.setId(2L);
        Quiz otherQuiz = new Quiz(); otherQuiz.setId(11L); otherQuiz.setAuthor(otherTeacher);
        it.bicocca.eduquest.domain.quiz.Test t2 = new it.bicocca.eduquest.domain.quiz.Test();
        t2.setId(101L);
        t2.setQuiz(otherQuiz);

        when(testRepository.findAll()).thenReturn(Arrays.asList(t1, t2));
        mockStats(t1);

        List<TestDTO> result = testServices.getTestsByTeacherId(1L);

        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getId());
    }
    
    @Test
    void deleteTest_Success() {
        Long testId = 100L;
        it.bicocca.eduquest.domain.quiz.Test mockTest = new it.bicocca.eduquest.domain.quiz.Test();
        mockTest.setId(testId);

        when(testRepository.findById(testId)).thenReturn(Optional.of(mockTest));
        
        testServices.deleteTest(testId);
        
        verify(testRepository).deleteById(testId);
    }

    @Test
    void deleteTest_NotFound() {
        Long testId = 999L;
        when(testRepository.findById(testId)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> testServices.deleteTest(testId));
        
        verify(testRepository, never()).deleteById(anyLong());
    }
}