package it.bicocca.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import it.bicocca.eduquest.domain.multimedia.MultimediaType; 
import it.bicocca.eduquest.domain.multimedia.ImageSupport;

import it.bicocca.eduquest.domain.quiz.Difficulty;

import it.bicocca.eduquest.domain.quiz.ClosedQuestion;
import it.bicocca.eduquest.domain.quiz.ClosedQuestionOption;
import it.bicocca.eduquest.domain.quiz.OpenQuestion;
import it.bicocca.eduquest.domain.quiz.Question;
import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.domain.users.Teacher;

import it.bicocca.eduquest.dto.quiz.ClosedQuestionOptionDTO;
import it.bicocca.eduquest.dto.quiz.QuestionAddDTO;
import it.bicocca.eduquest.dto.quiz.QuestionDTO;
import it.bicocca.eduquest.dto.quiz.QuestionType;
import it.bicocca.eduquest.dto.quiz.QuizAddDTO;
import it.bicocca.eduquest.dto.quiz.QuizDTO;
import it.bicocca.eduquest.dto.quiz.QuizEditDTO;
import it.bicocca.eduquest.repository.MultimediaRepository;
import it.bicocca.eduquest.repository.QuestionsRepository;
import it.bicocca.eduquest.repository.QuizAttemptsRepository;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
class QuizServicesTest {

    @Mock private QuizRepository quizRepository;
    @Mock private UsersRepository usersRepository;
    @Mock private QuestionsRepository questionsRepository;
    @Mock private MultimediaService multimediaService;
    @Mock private MultimediaRepository multimediaRepository;
    @Mock private QuizAttemptsRepository quizAttemptsRepository;

    @InjectMocks
    private QuizServices quizServices;

    private Teacher teacher;
    private Student student;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        teacher = new Teacher("Mario", "Rossi", "mario@test.com", "password");
        teacher.setId(1L);

        student = new Student("Luigi", "Verdi", "luigi@test.com", "password");
        student.setId(2L);

        quiz = new Quiz("Math Quiz", "Basic Math", teacher);
        quiz.setId(10L);
        quiz.setDifficulty(Difficulty.EASY); 
    }

    @Test
    void addQuizUserIsTeacher() {
        QuizAddDTO dto = new QuizAddDTO();
        dto.setTitle("History Quiz");
        dto.setDescription("Rome");

        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(quizRepository.save(any(Quiz.class))).thenAnswer(invocation -> {
            Quiz q = invocation.getArgument(0);
            q.setId(20L);
            return q;
        });

        QuizDTO result = quizServices.addQuiz(dto, 1L);

        assertNotNull(result);
        assertEquals("History Quiz", result.getTitle());
        assertEquals(1L, result.getTeacherAuthorId());
        verify(quizRepository).save(any(Quiz.class));
    }

    @Test
    void addQuiz_ShouldThrowException_WhenUserIsStudent() {
        QuizAddDTO dto = new QuizAddDTO();
        when(usersRepository.findById(2L)).thenReturn(Optional.of(student));

        assertThrows(IllegalArgumentException.class, () -> {
            quizServices.addQuiz(dto, 2L);
        });
    }

    @Test
    void getQuizById_ShouldReturnDtoWithStats() {
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        when(quizAttemptsRepository.getAverageScoreByQuizAndTestIsNull(quiz)).thenReturn(8.5);
        when(quizAttemptsRepository.countByQuizAndTestIsNull(quiz)).thenReturn(10L);

        QuizDTO result = quizServices.getQuizById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(8.5, result.getQuizStats().getAverageScore());
    }

    @Test
    void getQuizById_ShouldThrowException_WhenNotFound() {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.getQuizById(99L));
    }

    @Test
    void editQuiz_ShouldUpdate_WhenUserIsAuthor() {
        QuizEditDTO editDTO = new QuizEditDTO();
        editDTO.setTitle("New Title");
        editDTO.setDescription("New Desc");

        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);
        when(quizAttemptsRepository.getAverageScoreByQuizAndTestIsNull(any())).thenReturn(0.0);
        when(quizAttemptsRepository.countByQuizAndTestIsNull(any())).thenReturn(0L);

        QuizDTO result = quizServices.editQuiz(10L, editDTO, 1L);

        assertEquals("New Title", result.getTitle());
    }

    @Test
    void editQuiz_ShouldThrowException_WhenUserIsNotAuthor() {
        QuizEditDTO editDTO = new QuizEditDTO();
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));

        assertThrows(IllegalStateException.class, () -> {
            quizServices.editQuiz(10L, editDTO, 999L);
        });
    }

    @Test
    void addQuestion_ShouldCreateOpenQuestion() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.OPENED);
        dto.setText("What is 2+2?");
        dto.setTopic("Math");
        
        dto.setDifficulty(Difficulty.EASY);
        
        dto.setValidAnswersOpenQuestion(List.of("4", "Four"));

        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(questionsRepository.save(any(OpenQuestion.class))).thenAnswer(i -> {
            OpenQuestion q = i.getArgument(0);
            q.setId(50L);
            return q;
        });

        QuestionDTO result = quizServices.addQuestion(dto, 1L, null);

        assertNotNull(result);
        assertEquals("What is 2+2?", result.getText());
        assertEquals(QuestionType.OPENED, result.getQuestionType());
    }

    @Test
    void addQuestion_ShouldCreateClosedQuestion() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.CLOSED);
        dto.setText("Choose color");
        dto.setTopic("Art");
        
        dto.setDifficulty(Difficulty.MEDIUM);
        
        List<ClosedQuestionOptionDTO> options = new ArrayList<>();
        options.add(new ClosedQuestionOptionDTO(null, "Red", true));
        options.add(new ClosedQuestionOptionDTO(null, "Blue", false));
        dto.setClosedQuestionOptions(options);

        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(questionsRepository.save(any(ClosedQuestion.class))).thenAnswer(i -> {
            ClosedQuestion q = i.getArgument(0);
            q.setId(60L);
            return q;
        });

        QuestionDTO result = quizServices.addQuestion(dto, 1L, null);

        assertNotNull(result);
        assertEquals(QuestionType.CLOSED, result.getQuestionType());
    }
    
    @Test
    void addQuestion_WithImage_ShouldSaveMultimedia() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.OPENED);
        dto.setText("Describe image");
        dto.setTopic("Art");
        
        dto.setDifficulty(Difficulty.HARD);
        
        dto.setValidAnswersOpenQuestion(List.of("Mona Lisa"));
        
        dto.setMultimediaType(MultimediaType.IMAGE);

        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(multimediaService.uploadMedia(any(), anyString())).thenReturn("http://url.com/img.jpg");
        when(multimediaRepository.save(any(ImageSupport.class))).thenReturn(new ImageSupport());
        when(questionsRepository.save(any(Question.class))).thenAnswer(i -> {
            Question q = i.getArgument(0);
            q.setId(70L);
            return q;
        });

        QuestionDTO result = quizServices.addQuestion(dto, 1L, file);

        verify(multimediaService).uploadMedia(any(), eq("eduquest_images"));
        assertNotNull(result.getMultimedia());
    }

    @Test
    void addQuestionToQuiz_ShouldAddAndRecalculate() {
        OpenQuestion question = new OpenQuestion("Q1", "Topic", teacher, Difficulty.EASY);
        question.setId(100L);

        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(100L)).thenReturn(Optional.of(question));
        
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);
        when(quizAttemptsRepository.getAverageScoreByQuizAndTestIsNull(any())).thenReturn(0.0);
        when(quizAttemptsRepository.countByQuizAndTestIsNull(any())).thenReturn(0L);

        QuizDTO result = quizServices.addQuestionToQuiz(10L, 100L, 1L);

        assertEquals(1, result.getQuestions().size());
        assertEquals(100L, result.getQuestions().get(0).getId());
        verify(quizRepository).save(quiz);
    }
    
    @Test
    void addQuestionToQuiz_ShouldThrow_IfQuestionAlreadyPresent() {
        OpenQuestion question = new OpenQuestion("Q1", "Topic", teacher, Difficulty.EASY);
        question.setId(100L);
        quiz.addQuestion(question); 

        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(100L)).thenReturn(Optional.of(question));

        assertThrows(IllegalStateException.class, () -> {
            quizServices.addQuestionToQuiz(10L, 100L, 1L);
        });
    }

    @Test
    void removeQuestionFromQuiz_ShouldRemove() {
        OpenQuestion question = new OpenQuestion("Q1", "Topic", teacher, Difficulty.EASY);
        question.setId(100L);
        quiz.addQuestion(question); 

        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(100L)).thenReturn(Optional.of(question));
        
        when(quizAttemptsRepository.getAverageScoreByQuizAndTestIsNull(any())).thenReturn(0.0);
        when(quizAttemptsRepository.countByQuizAndTestIsNull(any())).thenReturn(0L);

        QuizDTO result = quizServices.removeQuestionFromQuiz(10L, 100L, 1L);

        assertEquals(0, result.getQuestions().size());
        verify(quizRepository).save(quiz);
    }
    
    @Test
    void getQuizForStudent_ShouldSanitizeCorrectAnswers() {
        ClosedQuestion cq = new ClosedQuestion("Q1", "Topic", teacher, Difficulty.MEDIUM);
        cq.setId(200L);
        cq.addOption(new ClosedQuestionOption("Right", true));
        cq.addOption(new ClosedQuestionOption("Wrong", false));
        quiz.addQuestion(cq);

        when(usersRepository.findById(2L)).thenReturn(Optional.of(student));
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        when(quizAttemptsRepository.getAverageScoreByQuizAndTestIsNull(any())).thenReturn(0.0);
        when(quizAttemptsRepository.countByQuizAndTestIsNull(any())).thenReturn(0L);

        QuizDTO result = quizServices.getQuizForStudent(10L, 2L);

        QuestionDTO qDto = result.getQuestions().get(0);
        for(ClosedQuestionOptionDTO opt : qDto.getClosedQuestionOptions()) {
            assertFalse(opt.isTrue());
        }
    }
}