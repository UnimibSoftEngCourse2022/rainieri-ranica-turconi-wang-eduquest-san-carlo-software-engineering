package it.bicocca.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.bicocca.eduquest.domain.quiz.*;
import it.bicocca.eduquest.domain.users.*;
import it.bicocca.eduquest.dto.quiz.*;
import it.bicocca.eduquest.repository.*;

@ExtendWith(MockitoExtension.class)
class QuizServicesTest {

    @Mock private QuizRepository quizRepository;
    @Mock private UsersRepository usersRepository;
    @Mock private QuestionsRepository questionsRepository;

    @InjectMocks
    private QuizServices quizServices;

    @Test
    void testGetQuizById_Success() {
        long quizId = 1L;
        Teacher author = new Teacher(); author.setId(10L);
        Quiz quiz = new Quiz(quizId, "Title", "Desc", author);
        
        OpenQuestion q = new OpenQuestion();
        q.setId(100L); q.setText("Q1"); q.setAuthor(author); q.setQuestionType(QuestionType.OPENED);
        quiz.addQuestion(q);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        QuizDTO result = quizServices.getQuizById(quizId);

        assertNotNull(result);
        assertEquals("Title", result.getTitle());
        assertEquals(1, result.getQuestions().size());
        assertEquals("Q1", result.getQuestions().get(0).getText());
    }

    @Test
    void testGetQuizById_NotFound() {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.getQuizById(99L));
    }

    @Test
    void testGetAllQuizzes() {
        Teacher author = new Teacher(); author.setId(1L);
        Quiz q1 = new Quiz(1L, "Q1", "D1", author);
        Quiz q2 = new Quiz(2L, "Q2", "D2", author);
        
        ClosedQuestion cq = new ClosedQuestion();
        cq.setId(50L); cq.setAuthor(author); cq.setQuestionType(QuestionType.CLOSED);
        cq.addOption(new ClosedQuestionOption("Opt1", true));
        q1.addQuestion(cq);

        when(quizRepository.findAll()).thenReturn(Arrays.asList(q1, q2));

        List<QuizDTO> result = quizServices.getAllQuizzes();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getQuestions().size());
    }

    @Test
    void testGetQuizzesByAuthorId() {
        long targetAuthorId = 1L;
        Teacher targetAuthor = new Teacher(); targetAuthor.setId(targetAuthorId);
        Teacher otherAuthor = new Teacher(); otherAuthor.setId(2L);

        Quiz q1 = new Quiz(10L, "My Quiz", "Desc", targetAuthor);
        Quiz q2 = new Quiz(11L, "Other Quiz", "Desc", otherAuthor);

        when(quizRepository.findAll()).thenReturn(Arrays.asList(q1, q2));

        List<QuizDTO> result = quizServices.getQuizzesByAuthorId(targetAuthorId);

        assertEquals(1, result.size());
        assertEquals("My Quiz", result.get(0).getTitle());
    }

    @Test
    void testAddQuiz_Success() {
        long teacherId = 5L;
        Teacher teacher = new Teacher(); teacher.setId(teacherId);
        QuizAddDTO dto = new QuizAddDTO(); dto.setTitle("New"); dto.setDescription("Desc");

        when(usersRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        
        when(quizRepository.save(any(Quiz.class))).thenAnswer(i -> {
            Quiz q = i.getArgument(0);
            q.setId(100L);
            return q;
        });

        QuizDTO result = quizServices.addQuiz(dto, teacherId);

        assertEquals(100L, result.getId());
        assertEquals("New", result.getTitle());
    }

    @Test
    void testAddQuiz_UserNotFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());
        QuizAddDTO dto = new QuizAddDTO();
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuiz(dto, 1L));
    }

    @Test
    void testAddQuiz_NotTeacher() {
        Student student = new Student();
        when(usersRepository.findById(2L)).thenReturn(Optional.of(student));
        QuizAddDTO dto = new QuizAddDTO();
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuiz(dto, 2L));
    }

    @Test
    void testEditQuiz_Success() {
        long quizId = 1L; long userId = 10L;
        Teacher author = new Teacher(); author.setId(userId);
        Quiz quiz = new Quiz(quizId, "Old", "Old", author);
        QuizEditDTO dto = new QuizEditDTO(); dto.setTitle("New"); dto.setDescription("New");

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        QuizDTO result = quizServices.editQuiz(quizId, dto, userId);

        assertEquals("New", result.getTitle());
    }

    @Test
    void testEditQuiz_QuizNotFound() {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());
        QuizEditDTO dto = new QuizEditDTO();
        assertThrows(IllegalArgumentException.class, () -> quizServices.editQuiz(99L, dto, 1L));
    }

    @Test
    void testEditQuiz_NotOwner() {
        Teacher author = new Teacher(); author.setId(10L);
        Quiz quiz = new Quiz(1L, "T", "D", author);
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        QuizEditDTO dto = new QuizEditDTO();
        assertThrows(IllegalStateException.class, () -> quizServices.editQuiz(1L, dto, 99L));
    }

    @Test
    void testGetAllQuestions_AsTeacher_SeeAll() {
        long teacherId = 1L;
        Teacher teacher = new Teacher(); teacher.setId(teacherId);
        Teacher other = new Teacher(); other.setId(2L);

        OpenQuestion q1 = new OpenQuestion("Q1", "T", teacher, Difficulty.EASY);
        q1.setId(10L); q1.setQuestionType(QuestionType.OPENED);
        q1.addAnswer(new OpenQuestionAcceptedAnswer("Ans"));

        ClosedQuestion q2 = new ClosedQuestion("Q2", "T", other, Difficulty.HARD);
        q2.setId(11L); q2.setQuestionType(QuestionType.CLOSED);
        q2.addOption(new ClosedQuestionOption("Opt", true));

        when(usersRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(questionsRepository.findAll()).thenReturn(Arrays.asList(q1, q2));

        List<QuestionDTO> result = quizServices.getAllQuestions(teacherId);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getValidAnswersOpenQuestion().size());
        assertEquals(1, result.get(1).getClosedQuestionOptions().size());
    }

    @Test
    void testGetAllQuestions_UserNotFound() {
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.getAllQuestions(99L));
    }

    @Test
    void testGetQuestionsByAuthorId() {
        long requestUserId = 1L;
        Teacher teacher = new Teacher(); teacher.setId(requestUserId);
        
        OpenQuestion q1 = new OpenQuestion("Q1", "Topic", teacher, Difficulty.EASY);
        q1.setId(10L); q1.setQuestionType(QuestionType.OPENED);
        
        Teacher other = new Teacher(); other.setId(2L);
        OpenQuestion q2 = new OpenQuestion("Q2", "Topic", other, Difficulty.EASY);
        q2.setId(20L); q2.setQuestionType(QuestionType.OPENED);

        when(usersRepository.findById(requestUserId)).thenReturn(Optional.of(teacher));
        when(questionsRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(q1, q2)));

        List<QuestionDTO> result = quizServices.getQuestionsByAuthorId(requestUserId, requestUserId);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
    }

    @Test
    void testAddQuestion_Open_Success() {
        long userId = 1L;
        Teacher author = new Teacher(); author.setId(userId);
        
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setText("Text"); dto.setTopic("Topic");
        dto.setQuestionType(QuestionType.OPENED);
        dto.setValidAnswersOpenQuestion(Arrays.asList("A1", "A2"));

        when(usersRepository.findById(userId)).thenReturn(Optional.of(author));
        when(questionsRepository.save(any(Question.class))).thenAnswer(i -> {
            Question q = i.getArgument(0);
            q.setId(500L);
            return q;
        });

        QuestionDTO result = quizServices.addQuestion(dto, userId);

        assertEquals(500L, result.getId());
        assertEquals(QuestionType.OPENED, result.getQuestionType());
        assertEquals(2, result.getValidAnswersOpenQuestion().size());
    }

    @Test
    void testAddQuestion_Closed_Success() {
        long userId = 1L;
        Teacher author = new Teacher(); author.setId(userId);
        
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setText("Text"); dto.setTopic("Topic");
        dto.setQuestionType(QuestionType.CLOSED);
        ClosedQuestionOptionDTO cqo1 = new ClosedQuestionOptionDTO();
        cqo1.setText("Opt1"); cqo1.setTrue(true);
        
        ClosedQuestionOptionDTO cqo2 = new ClosedQuestionOptionDTO();
        cqo2.setText("Opt2"); cqo2.setTrue(false);
        
        dto.setClosedQuestionOptions(Arrays.asList(cqo1, cqo2));

        when(usersRepository.findById(userId)).thenReturn(Optional.of(author));
        when(questionsRepository.save(any(Question.class))).thenAnswer(i -> {
            Question q = i.getArgument(0);
            q.setId(600L);
            return q;
        });

        QuestionDTO result = quizServices.addQuestion(dto, userId);

        assertEquals(600L, result.getId());
        assertEquals(QuestionType.CLOSED, result.getQuestionType());
        assertNotNull(result.getClosedQuestionOptions());
        assertEquals(2, result.getClosedQuestionOptions().size());
    }

    @Test
    void testAddQuestion_ValidationErrors() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(new Teacher()));
        
        QuestionAddDTO dto1 = new QuestionAddDTO(); dto1.setText("");
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto1, 1L));

        QuestionAddDTO dto2 = new QuestionAddDTO(); dto2.setText("Ok"); dto2.setTopic(null);
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto2, 1L));

        QuestionAddDTO dto3 = new QuestionAddDTO(); dto3.setText("Ok"); dto3.setTopic("Topic"); 
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto3, 1L));
    }
    
    @Test
    void testAddQuestion_UserNotFound() {
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());
        QuestionAddDTO dto = new QuestionAddDTO();
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto, 99L));
    }

    @Test
    void testAddQuestionToQuiz_Success() {
        long quizId = 1L; long qId = 2L; long userId = 10L;
        Teacher author = new Teacher(); author.setId(userId);
        Quiz quiz = new Quiz(quizId, "T", "D", author);
        Question question = new OpenQuestion(); question.setId(qId); question.setAuthor(author); question.setQuestionType(QuestionType.OPENED);
        question.setDifficulty(Difficulty.EASY);
        
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(qId)).thenReturn(Optional.of(question));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        QuizDTO result = quizServices.addQuestionToQuiz(quizId, qId, userId);

        assertEquals(1, result.getQuestions().size());
    }

    @Test
    void testAddQuestionToQuiz_Duplicate() {
        long userId = 10L;
        Teacher author = new Teacher(); author.setId(userId);
        Quiz quiz = new Quiz(1L, "T", "D", author);
        Question question = new OpenQuestion(); question.setId(2L); question.setAuthor(author);
        quiz.addQuestion(question); 

        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(2L)).thenReturn(Optional.of(question));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> quizServices.addQuestionToQuiz(1L, 2L, userId));
        assertEquals("Question already included in the quiz!", ex.getMessage());
    }

    @Test
    void testAddQuestionToQuiz_NotOwner() {
        Teacher author = new Teacher(); author.setId(10L);
        Quiz quiz = new Quiz(1L, "T", "D", author);
        Question question = new OpenQuestion(); question.setId(2L);
        
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(2L)).thenReturn(Optional.of(question));
        
        assertThrows(IllegalStateException.class, () -> quizServices.addQuestionToQuiz(1L, 2L, 99L));
    }
    
    @Test
    void testAddQuestionToQuiz_NotFound() {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestionToQuiz(99L, 1L, 1L));
        
        when(quizRepository.findById(1L)).thenReturn(Optional.of(new Quiz()));
        when(questionsRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestionToQuiz(1L, 99L, 1L));
    }

    @Test
    void testRemoveQuestionFromQuiz_Success() {
        long quizId = 1L; long qId = 2L; long userId = 10L;
        Teacher author = new Teacher(); author.setId(userId);
        Quiz quiz = new Quiz(quizId, "T", "D", author);
        Question question = new OpenQuestion(); question.setId(qId); question.setAuthor(author); question.setQuestionType(QuestionType.OPENED);
        quiz.addQuestion(question);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(qId)).thenReturn(Optional.of(question));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        QuizDTO result = quizServices.removeQuestionFromQuiz(quizId, qId, userId);

        assertEquals(0, result.getQuestions().size());
    }
    
    @Test
    void testRemoveQuestionFromQuiz_Errors() {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.removeQuestionFromQuiz(99L, 1L, 1L));
        
        when(quizRepository.findById(1L)).thenReturn(Optional.of(new Quiz()));
        when(questionsRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.removeQuestionFromQuiz(1L, 99L, 1L));
        
        Teacher author = new Teacher(); author.setId(10L);
        Quiz quiz = new Quiz(1L, "T", "D", author);
        Question question = new OpenQuestion(); question.setId(2L);
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(2L)).thenReturn(Optional.of(question));
        assertThrows(IllegalStateException.class, () -> quizServices.removeQuestionFromQuiz(1L, 2L, 99L));
    }

    @Test
    void testGetQuizForStudent_Success() {
        long quizId = 1L; long studentId = 5L;
        Student student = new Student(); student.setId(studentId);
        Teacher author = new Teacher(); author.setId(10L);

        Quiz quiz = new Quiz(quizId, "T", "D", author);
        
        OpenQuestion q1 = new OpenQuestion(); q1.setId(10L); q1.setAuthor(author); q1.setQuestionType(QuestionType.OPENED);
        q1.addAnswer(new OpenQuestionAcceptedAnswer("Secret"));
        
        ClosedQuestion q2 = new ClosedQuestion(); q2.setId(20L); q2.setAuthor(author); q2.setQuestionType(QuestionType.CLOSED);
        q2.addOption(new ClosedQuestionOption("A", true)); 
        q2.addOption(new ClosedQuestionOption("B", false));

        quiz.addQuestion(q1);
        quiz.addQuestion(q2);

        when(usersRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        QuizDTO result = quizServices.getQuizForStudent(quizId, studentId);

        assertNull(result.getQuestions().get(0).getValidAnswersOpenQuestion());
        
        List<ClosedQuestionOptionDTO> opts = result.getQuestions().get(1).getClosedQuestionOptions();
        assertFalse(opts.get(0).isTrue()); 
        assertFalse(opts.get(1).isTrue());
    }

    @Test
    void testGetQuizForStudent_NotStudent() {
        Teacher teacher = new Teacher(); teacher.setId(2L);
        when(usersRepository.findById(2L)).thenReturn(Optional.of(teacher));
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> quizServices.getQuizForStudent(1L, 2L));
        assertEquals("Given ID is associated to a Teacher, not a Student", ex.getMessage());
    }

    @Test
    void testGetQuizForStudent_UserNotFound() {
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.getQuizForStudent(1L, 99L));
    }
    
    @Test
    void testGetQuizForStudent_QuizNotFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(new Student()));
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.getQuizForStudent(99L, 1L));
    }
}