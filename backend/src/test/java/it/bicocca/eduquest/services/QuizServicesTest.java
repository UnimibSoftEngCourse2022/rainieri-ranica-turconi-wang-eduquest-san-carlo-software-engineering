package it.bicocca.eduquest.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.bicocca.eduquest.domain.quiz.OpenQuestion;
import it.bicocca.eduquest.domain.quiz.Question;
import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.domain.users.Teacher;
import it.bicocca.eduquest.dto.quiz.QuestionType;
import it.bicocca.eduquest.dto.quiz.QuizAddDTO;
import it.bicocca.eduquest.dto.quiz.QuizDTO;
import it.bicocca.eduquest.dto.quiz.QuizEditDTO;
import it.bicocca.eduquest.repository.QuestionsRepository;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
class QuizServicesTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private QuestionsRepository questionsRepository;

    @InjectMocks
    private QuizServices quizServices;

    @Test
    void testGetQuizById_Success() {
        long quizId = 1L;
        Teacher author = new Teacher();
        author.setId(10L);

        Quiz quiz = new Quiz(quizId, "Test Quiz", "Description", author);
        
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        QuizDTO result = quizServices.getQuizById(quizId);

        assertNotNull(result);
        assertEquals("Test Quiz", result.getTitle());
        assertEquals(10L, result.getTeacherAuthorId());
    }

    @Test
    void testGetQuizById_NotFound() {
        long quizId = 99L;
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            quizServices.getQuizById(quizId);
        });

        assertEquals("Cannot find quiz with ID 99", exception.getMessage());
    }

    @Test
    void testAddQuiz_Success_Teacher() {
        long teacherId = 5L;
        QuizAddDTO quizAddDTO = new QuizAddDTO();
        quizAddDTO.setTitle("New Quiz");
        quizAddDTO.setDescription("Hard Difficulty");

        Teacher teacher = new Teacher();
        teacher.setId(teacherId);

        when(usersRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        Quiz savedQuiz = new Quiz(100L, "New Quiz", "Hard Difficulty", teacher);
        when(quizRepository.save(any(Quiz.class))).thenReturn(savedQuiz);

        QuizDTO result = quizServices.addQuiz(quizAddDTO, teacherId);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("New Quiz", result.getTitle());
    }

    @Test
    void testAddQuiz_Fail_Student() {
        long studentId = 2L;
        QuizAddDTO quizAddDTO = new QuizAddDTO();
        Student student = new Student();

        when(usersRepository.findById(studentId)).thenReturn(Optional.of(student));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            quizServices.addQuiz(quizAddDTO, studentId);
        });

        assertEquals("Given ID is associated to a Student, not a Teacher", exception.getMessage());
        verify(quizRepository, never()).save(any());
    }

    @Test
    void testEditQuiz_Success_Owner() {
        long quizId = 1L;
        long ownerId = 10L;

        QuizEditDTO editDTO = new QuizEditDTO();
        editDTO.setTitle("Edited Title");
        editDTO.setDescription("Edited Description");

        Teacher author = new Teacher();
        author.setId(ownerId);

        Quiz existingQuiz = new Quiz(quizId, "Old Title", "Old Description", author);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(existingQuiz));
        when(quizRepository.save(any(Quiz.class))).thenAnswer(invocation -> invocation.getArgument(0));

        QuizDTO result = quizServices.editQuiz(quizId, editDTO, ownerId);

        assertEquals("Edited Title", result.getTitle());
        assertEquals("Edited Description", result.getDescription());
    }

    @Test
    void testEditQuiz_Fail_NotOwner() {
        long quizId = 1L;
        long ownerId = 10L;
        long hackerId = 99L;

        Teacher author = new Teacher();
        author.setId(ownerId);

        Quiz existingQuiz = new Quiz(quizId, "Title", "Desc", author);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(existingQuiz));

        QuizEditDTO editDTO = new QuizEditDTO();
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            quizServices.editQuiz(quizId, editDTO, hackerId);
        });

        assertEquals("You cannot edit quiz from another author!", exception.getMessage());
        verify(quizRepository, never()).save(any());
    }

    @Test
    void testAddQuestionToQuiz_Success() {
        long quizId = 1L;
        long questionId = 20L;
        long userId = 5L;

        Teacher author = new Teacher();
        author.setId(userId);
        
        Quiz quiz = new Quiz(quizId, "Test Quiz", "Desc", author);
        
        Question question = new OpenQuestion();
        question.setId(questionId);
        question.setText("Open Question");
        question.setQuestionType(QuestionType.OPENED);
        question.setAuthor(author);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        QuizDTO result = quizServices.addQuestionToQuiz(quizId, questionId, userId);

        verify(quizRepository, times(1)).save(quiz);
        assertNotNull(result.getQuestions());
        assertEquals(1, result.getQuestions().size());
    }
}