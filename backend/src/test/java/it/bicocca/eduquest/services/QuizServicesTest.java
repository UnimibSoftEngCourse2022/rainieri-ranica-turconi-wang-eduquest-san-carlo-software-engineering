package it.bicocca.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import it.bicocca.eduquest.domain.multimedia.VideoSupport;
import it.bicocca.eduquest.domain.quiz.Difficulty;
import it.bicocca.eduquest.domain.quiz.ClosedQuestion;
import it.bicocca.eduquest.domain.quiz.ClosedQuestionOption;
import it.bicocca.eduquest.domain.quiz.OpenQuestion;
import it.bicocca.eduquest.domain.quiz.OpenQuestionAcceptedAnswer;
import it.bicocca.eduquest.domain.quiz.Question;
import it.bicocca.eduquest.domain.quiz.QuestionStats;
import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.quiz.QuizStats;
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
    void addQuizTitleIsEmpty() {
        QuizAddDTO dto = new QuizAddDTO();
        dto.setTitle("   ");
        dto.setDescription("Valid Desc");

        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));

        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuiz(dto, 1L));
    }

    @Test
    void addQuizDescriptionIsNull() {
        QuizAddDTO dto = new QuizAddDTO();
        dto.setTitle("Valid Title");
        dto.setDescription(null); 

        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));

        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuiz(dto, 1L));
    }

    @Test
    void getQuizByIdReturnDtoWithStats() {
        QuizStats quizStats = mock(QuizStats.class);
        Map<Long, QuestionStats> statsMap = new HashMap<>();
        
        QuestionStats qStats = mock(QuestionStats.class);
        when(qStats.getAverageSuccess()).thenReturn(0.8);
        when(qStats.getTotalAnswers()).thenReturn(50);
        when(qStats.getCorrectAnswer()).thenReturn(40);
        
        statsMap.put(100L, qStats);
        when(quizStats.getStatsPerQuestion()).thenReturn(statsMap);
        
        Quiz quizWithStats = mock(Quiz.class);
        when(quizWithStats.getId()).thenReturn(10L);
        when(quizWithStats.getTitle()).thenReturn("Title");
        when(quizWithStats.getDescription()).thenReturn("Desc");
        when(quizWithStats.getAuthor()).thenReturn(teacher);
        when(quizWithStats.getDifficulty()).thenReturn(Difficulty.EASY);
        when(quizWithStats.getStats()).thenReturn(quizStats);
        when(quizWithStats.getQuestions()).thenReturn(new ArrayList<>());

        when(quizRepository.findById(10L)).thenReturn(Optional.of(quizWithStats));
        when(quizAttemptsRepository.getAverageScoreByQuizAndTestIsNull(quizWithStats)).thenReturn(8.5);
        when(quizAttemptsRepository.countByQuizAndTestIsNull(quizWithStats)).thenReturn(10L);

        QuizDTO result = quizServices.getQuizById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(8.5, result.getQuizStats().getAverageScore());
        assertTrue(result.getQuizStats().getStatsPerQuestion().containsKey(100L));
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
    void editQuizTitleEmpty() {
        QuizEditDTO dto = new QuizEditDTO();
        dto.setTitle(""); 
        dto.setDescription("Desc");

        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));

        assertThrows(IllegalArgumentException.class, () -> quizServices.editQuiz(10L, dto, 1L));
    }

    @Test
    void editQuizDescriptionEmpty() {
        QuizEditDTO dto = new QuizEditDTO();
        dto.setTitle("Title"); 
        dto.setDescription("   ");

        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));

        assertThrows(IllegalArgumentException.class, () -> quizServices.editQuiz(10L, dto, 1L));
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
    void addQuestionUserNotFound() {
        QuestionAddDTO dto = new QuestionAddDTO();
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto, 99L, null));
    }

    @Test
    void addQuestionTextEmpty() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setText("");
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto, 1L, null));
    }

    @Test
    void addQuestionTopicEmpty() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setText("Text");
        dto.setTopic(null);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto, 1L, null));
    }

    @Test
    void addQuestionTypeNull() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setText("Text");
        dto.setTopic("Topic");
        dto.setQuestionType(null); 
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto, 1L, null));
    }

    @Test
    void addQuestionOpenNoAnswers() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.OPENED);
        dto.setText("Text");
        dto.setTopic("Topic");
        dto.setValidAnswersOpenQuestion(null); 
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto, 1L, null));
    }

    @Test
    void addQuestionOpenEmptyAnswer() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.OPENED);
        dto.setText("Text");
        dto.setTopic("Topic");
        List<String> answers = new ArrayList<>();
        answers.add("");
        dto.setValidAnswersOpenQuestion(answers);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto, 1L, null));
    }

    @Test
    void addQuestionClosedOptionsNull() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.CLOSED);
        dto.setText("Text");
        dto.setTopic("Topic");
        dto.setClosedQuestionOptions(null);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto, 1L, null));
    }

    @Test
    void addQuestionClosedTooFewOptions() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.CLOSED);
        dto.setText("Text");
        dto.setTopic("Topic");
        dto.setClosedQuestionOptions(List.of(new ClosedQuestionOptionDTO(null, "A", true))); 
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto, 1L, null));
    }

    @Test
    void addQuestionClosedTooManyOptions() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.CLOSED);
        dto.setText("Text");
        dto.setTopic("Topic");
        List<ClosedQuestionOptionDTO> options = new ArrayList<>();
        for(int i=0; i<5; i++) options.add(new ClosedQuestionOptionDTO(null, "Opt"+i, i==0));
        dto.setClosedQuestionOptions(options);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto, 1L, null));
    }

    @Test
    void addQuestionClosedOptionTextEmpty() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.CLOSED);
        dto.setText("Text");
        dto.setTopic("Topic");
        List<ClosedQuestionOptionDTO> options = new ArrayList<>();
        options.add(new ClosedQuestionOptionDTO(null, "A", true));
        options.add(new ClosedQuestionOptionDTO(null, "", false));
        dto.setClosedQuestionOptions(options);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto, 1L, null));
    }

    @Test
    void addQuestionClosedNoCorrectAnswer() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.CLOSED);
        dto.setText("Text");
        dto.setTopic("Topic");
        List<ClosedQuestionOptionDTO> options = new ArrayList<>();
        options.add(new ClosedQuestionOptionDTO(null, "A", false));
        options.add(new ClosedQuestionOptionDTO(null, "B", false));
        dto.setClosedQuestionOptions(options);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto, 1L, null));
    }
    
    @Test
    void addQuestionMultimediaImage() {
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
    void addQuestionMultimediaAudio() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.OPENED);
        dto.setText("Listen");
        dto.setTopic("Audio");
        dto.setDifficulty(Difficulty.EASY);
        dto.setValidAnswersOpenQuestion(List.of("OK"));
        dto.setMultimediaType(MultimediaType.AUDIO);

        MockMultipartFile file = new MockMultipartFile("file", "audio.mp3", "audio/mpeg", "sound".getBytes());

        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(multimediaService.uploadMedia(any(), eq("eduquest_audios"))).thenReturn("http://audio.url");
        when(multimediaRepository.save(any())).thenReturn(null);
        when(questionsRepository.save(any(Question.class))).thenAnswer(i -> {
            Question q = i.getArgument(0);
            q.setId(90L);
            return q;
        });

        QuestionDTO result = quizServices.addQuestion(dto, 1L, file);

        verify(multimediaService).uploadMedia(any(), eq("eduquest_audios"));
        assertEquals("http://audio.url", result.getMultimedia().getUrl());
    }

    @Test
    void addQuestionMultimediaVideo() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.OPENED);
        dto.setText("Watch");
        dto.setTopic("Video");
        dto.setDifficulty(Difficulty.EASY);
        dto.setValidAnswersOpenQuestion(List.of("OK"));
        dto.setMultimediaType(MultimediaType.VIDEO);

        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", "video".getBytes());

        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(multimediaService.uploadMedia(any(), eq("eduquest_videos"))).thenReturn("http://video.url");
        when(multimediaRepository.save(any())).thenReturn(null);
        when(questionsRepository.save(any(Question.class))).thenAnswer(i -> {
            Question q = i.getArgument(0);
            q.setId(91L);
            return q;
        });

        QuestionDTO result = quizServices.addQuestion(dto, 1L, file);

        verify(multimediaService).uploadMedia(any(), eq("eduquest_videos"));
        assertEquals("http://video.url", result.getMultimedia().getUrl());
        assertFalse(result.getMultimedia().getIsYoutube()); 
    }

    @Test
    void addQuestionMultimediaVideoUrl() {
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
    void addQuestionMultimediaNotSupported() {
        QuestionAddDTO dto = new QuestionAddDTO();
        dto.setQuestionType(QuestionType.OPENED);
        dto.setText("Text");
        dto.setTopic("Topic");
        dto.setValidAnswersOpenQuestion(List.of("Ok"));
        dto.setMultimediaType(null); 
        MockMultipartFile file = new MockMultipartFile("file", "content".getBytes());
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestion(dto, 1L, file));
    }

    @Test
    void addQuestionToQuizSuccess() {
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
    }
    
    @Test
    void addQuestionToQuizAlreadyPresent() {
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
    void addQuestionToQuizNotAuthor() {
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(anyLong())).thenReturn(Optional.of(new OpenQuestion()));

        assertThrows(IllegalStateException.class, () -> {
            quizServices.addQuestionToQuiz(10L, 100L, 99L);
        });
    }

    @Test
    void addQuestionToQuizQuizNotFound() {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestionToQuiz(99L, 100L, 1L));
    }

    @Test
    void addQuestionToQuizQuestionNotFound() {
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.addQuestionToQuiz(10L, 999L, 1L));
    }

    @Test
    void removeQuestionFromQuizSuccess() {
        OpenQuestion question = new OpenQuestion("Q1", "Topic", teacher, Difficulty.EASY);
        question.setId(100L);
        quiz.addQuestion(question); 

        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(100L)).thenReturn(Optional.of(question));
        when(quizRepository.save(any(Quiz.class))).thenAnswer(i -> i.getArgument(0));
        when(quizAttemptsRepository.getAverageScoreByQuizAndTestIsNull(any())).thenReturn(0.0);
        when(quizAttemptsRepository.countByQuizAndTestIsNull(any())).thenReturn(0L);

        QuizDTO result = quizServices.removeQuestionFromQuiz(10L, 100L, 1L);

        assertTrue(result.getQuestions().isEmpty());
    }

    @Test
    void removeQuestionFromQuizNotAuthor() {
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(100L)).thenReturn(Optional.of(new OpenQuestion()));
        assertThrows(IllegalStateException.class, () -> quizServices.removeQuestionFromQuiz(10L, 100L, 99L));
    }

    @Test
    void removeQuestionFromQuizQuizNotFound() {
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.removeQuestionFromQuiz(99L, 100L, 1L));
    }

    @Test
    void removeQuestionFromQuizQuestionNotFound() {
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        when(questionsRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.removeQuestionFromQuiz(10L, 999L, 1L));
    }
    
    @Test
    void getQuizForStudentSuccess() {
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
    void getQuizForStudentWithMultimediaAndOpenQuestion() {
        OpenQuestion q = new OpenQuestion("QOpen", "Topic", teacher, Difficulty.EASY);
        q.setId(300L);
        q.addAnswer(new OpenQuestionAcceptedAnswer("Answer"));
        
        VideoSupport vid = new VideoSupport();
        vid.setUrl("link");
        vid.setIsYoutube(true);
        q.setMultimedia(vid);
        
        quiz.addQuestion(q);

        when(usersRepository.findById(2L)).thenReturn(Optional.of(student));
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));
        when(quizAttemptsRepository.getAverageScoreByQuizAndTestIsNull(any())).thenReturn(0.0);
        when(quizAttemptsRepository.countByQuizAndTestIsNull(any())).thenReturn(0L);

        QuizDTO result = quizServices.getQuizForStudent(10L, 2L);

        assertNotNull(result);
        assertEquals(1, result.getQuestions().size());
        QuestionDTO qDto = result.getQuestions().get(0);
        assertEquals(QuestionType.OPENED, qDto.getQuestionType());
        assertNotNull(qDto.getMultimedia());
        assertTrue(qDto.getMultimedia().getIsYoutube());
    }

    @Test
    void getQuizForStudentNotStudent() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        assertThrows(IllegalArgumentException.class, () -> quizServices.getQuizForStudent(10L, 1L));
    }

    @Test
    void getQuizForStudentUserNotFound() {
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.getQuizForStudent(10L, 99L));
    }

    @Test
    void getQuizForStudentQuizNotFound() {
        when(usersRepository.findById(2L)).thenReturn(Optional.of(student));
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.getQuizForStudent(99L, 2L));
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

    //TODO refactor this test
    /*@Test
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
    }*/

    @Test
    void getAllQuestionsReturnAllWithDetails() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher));
        
        OpenQuestion q1 = new OpenQuestion("Q1", "T", teacher, Difficulty.EASY);
        q1.setId(10L);
        q1.setQuestionType(QuestionType.OPENED);
        q1.addAnswer(new OpenQuestionAcceptedAnswer("Ans1"));
        
        ClosedQuestion q2 = new ClosedQuestion("Q2", "T", teacher, Difficulty.MEDIUM);
        q2.setId(20L);
        q2.setQuestionType(QuestionType.CLOSED);
        q2.addOption(new ClosedQuestionOption("Opt1", true));
        
        when(questionsRepository.findAll()).thenReturn(List.of(q1, q2));

        List<QuestionDTO> result = quizServices.getAllQuestions(1L);

        assertEquals(2, result.size());
        
        QuestionDTO dto1 = result.stream().filter(d -> d.getId() == 10L).findFirst().get();
        assertFalse(dto1.getValidAnswersOpenQuestion().isEmpty());
        assertEquals("Ans1", dto1.getValidAnswersOpenQuestion().get(0));
        
        QuestionDTO dto2 = result.stream().filter(d -> d.getId() == 20L).findFirst().get();
        assertFalse(dto2.getClosedQuestionOptions().isEmpty());
        assertEquals("Opt1", dto2.getClosedQuestionOptions().get(0).getText());
    }

    @Test
    void getAllQuestionsReturnOnlyOwn() {
        when(usersRepository.findById(2L)).thenReturn(Optional.of(student));
        
        Question q = new OpenQuestion("Q1", "T", student, Difficulty.EASY);
        q.setId(11L);
        when(questionsRepository.findByAuthorId(2L)).thenReturn(List.of(q));

        List<QuestionDTO> result = quizServices.getAllQuestions(2L);

        assertEquals(1, result.size());
        verify(questionsRepository).findByAuthorId(2L);
    }
    
    @Test
    void getAllQuestionsUserNotFound() {
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> quizServices.getAllQuestions(99L));
    }

    @Test
    void getQuestionsByAuthorIdFilter() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(teacher)); 
        
        Question q1 = new OpenQuestion("Q1", "T", teacher, Difficulty.EASY); 
        q1.setId(10L);
        Question q2 = new OpenQuestion("Q2", "T", student, Difficulty.EASY); 
        q2.setId(11L);
        
        when(questionsRepository.findAll()).thenReturn(List.of(q1, q2));

        List<QuestionDTO> result = quizServices.getQuestionsByAuthorId(1L, 1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId()); 
    }
}