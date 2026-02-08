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
    void addQuizUserIsStudent() {
        QuizAddDTO dto = new QuizAddDTO();
        when(usersRepository.findById(2L)).thenReturn(Optional.of(student));

        assertThrows(IllegalArgumentException.class, () -> {
            quizServices.addQuiz(dto, 2L);
        });
    }

    @Test
    void getQuizByIdReturnDtoWithStats() {
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        when(quizAttemptsRepository.getAverageScoreByQuizAndTestIsNull(quiz)).thenReturn(8.5);
        when(quizAttemptsRepository.countByQuizAndTestIsNull(quiz)).thenReturn(10L);

        QuizDTO result = quizServices.getQuizById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(8.5, result.getQuizStats().getAverageScore());
    }

    @Test
    void getQuizByIdNotFound() {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.getQuizById(99L));
    }

    @Test
    void editQuizUserIsAuthor() {
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
    void editQuizUserIsNotAuthor() {
        QuizEditDTO editDTO = new QuizEditDTO();
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));

        assertThrows(IllegalStateException.class, () -> {
            quizServices.editQuiz(10L, editDTO, 999L);
        });
    }

    @Test
    void addQuestionCreateOpenQuestion() {
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
    void addQuestionCreateClosedQuestion() {
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
    void addQuestionMultimedia() {
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
    void addQuestionToQuizAndRecalculate() {
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
    void addQuestionToQuizQuestionAlreadyPresent() {
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
    void removeQuestionFromQuiz() {
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
    void getQuizForStudentCorrectAnswers() {
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
    
    @Test
    void getAllQuizzesReturnList() {
        when(quizRepository.findAll()).thenReturn(List.of(quiz));
        when(quizAttemptsRepository.getAverageScoreByQuizAndTestIsNull(any())).thenReturn(0.0);
        when(quizAttemptsRepository.countByQuizAndTestIsNull(any())).thenReturn(0L);

        List<QuizDTO> results = quizServices.getAllQuizzes();

        assertEquals(1, results.size());
        assertEquals("Math Quiz", results.get(0).getTitle());
    }

    @Test
    void getQuizzesByAuthorIdFilterCorrectly() {
        Quiz quiz2 = new Quiz("Other", "Desc", new Teacher());
        quiz2.setId(11L);
        quiz2.getAuthor().setId(99L); 

        when(quizRepository.findAll()).thenReturn(List.of(quiz, quiz2));
        
        when(quizAttemptsRepository.getAverageScoreByQuizAndTestIsNull(quiz)).thenReturn(0.0);
        when(quizAttemptsRepository.countByQuizAndTestIsNull(quiz)).thenReturn(0L);

        List<QuizDTO> results = quizServices.getQuizzesByAuthorId(1L);

        assertEquals(1, results.size());
        assertEquals(10L, results.get(0).getId());
    }
    
    @Test
    void addQuestionNoCorrectAnswer() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.CLOSED);
        dto.setText("Text");
        dto.setTopic("Topic");
        dto.setDifficulty(Difficulty.MEDIUM);
        
        List<ClosedQuestionOptionDTO> options = new ArrayList<>();
        options.add(new ClosedQuestionOptionDTO(null, "A", false));
        options.add(new ClosedQuestionOptionDTO(null, "B", false));
        dto.setClosedQuestionOptions(options);

        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            quizServices.addQuestion(dto, 1L, null);
        });
        assertTrue(e.getMessage().contains("must select at least one correct answer"));
    }

    @Test
    void addQuestionTooFewOptions() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.CLOSED);
        dto.setText("Text");
        dto.setTopic("Topic");
        dto.setClosedQuestionOptions(List.of(new ClosedQuestionOptionDTO(null, "A", true))); 

        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            quizServices.addQuestion(dto, 1L, null);
        });
        assertTrue(e.getMessage().contains("must have at least 2 options"));
    }
    
    @Test
    void addQuestionSaveUrl() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.OPENED);
        dto.setText("Watch video");
        dto.setTopic("Media");
        dto.setDifficulty(Difficulty.EASY);
        dto.setValidAnswersOpenQuestion(List.of("OK"));
        
        dto.setMultimediaType(MultimediaType.VIDEO);
        dto.setMultimediaUrl("https://youtube.com/watch?v=123");

        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(multimediaRepository.save(any())).thenReturn(null); 
        
        when(questionsRepository.save(any(Question.class))).thenAnswer(i -> {
            Question q = i.getArgument(0);
            q.setId(80L); 
            return q;
        });

        QuestionDTO result = quizServices.addQuestion(dto, 1L, null);

        assertNotNull(result.getMultimedia());
        assertEquals("https://youtube.com/watch?v=123", result.getMultimedia().getUrl());
        assertTrue(result.getMultimedia().getIsYoutube());
    }
    
    @Test
    void addQuestionToQuizUserNotAuthor() {
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(anyLong())).thenReturn(Optional.of(new OpenQuestion()));

        assertThrows(IllegalStateException.class, () -> {
            quizServices.addQuestionToQuiz(10L, 100L, 99L);
        });
    }
}