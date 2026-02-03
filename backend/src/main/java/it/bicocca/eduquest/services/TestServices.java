package it.bicocca.eduquest.services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.quiz.Test;
import it.bicocca.eduquest.domain.users.Teacher;
import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.dto.quiz.QuizDTO;
import it.bicocca.eduquest.dto.quiz.TestAddDTO;
import it.bicocca.eduquest.dto.quiz.TestDTO;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.repository.TestRepository;
import it.bicocca.eduquest.repository.UsersRepository;

@Service
public class TestServices {

    private final TestRepository testRepository;
    private final QuizRepository quizRepository;
    private final UsersRepository usersRepository;
    private final QuizServices quizServices; // Per riutilizzare la conversione Quiz -> QuizDTO

    public TestServices(TestRepository testRepository, QuizRepository quizRepository, 
                        UsersRepository usersRepository, QuizServices quizServices) {
        this.testRepository = testRepository;
        this.quizRepository = quizRepository;
        this.usersRepository = usersRepository;
        this.quizServices = quizServices;
    }

    public TestDTO createTest(TestAddDTO dto, long teacherId) {
        User user = usersRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user with ID " + teacherId));
        
        if (!(user instanceof Teacher)) {
            throw new IllegalArgumentException("Only teachers can create tests");
        }

        Quiz quiz = quizRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find Quiz with ID " + dto.getQuizId()));

        if (!quiz.getAuthor().getId().equals(teacherId)) {
            throw new IllegalStateException("You cannot create a test using a quiz you don't own!");
        }
        
        Duration duration = null;
        if (dto.getMaxDurationMinutes() != null) {
            duration = Duration.ofMillis(dto.getMaxDurationMinutes());
        }

        Test test = new Test();
        test.setQuiz(quiz);
        test.setMaxDuration(duration);
        test.setMaxTries(dto.getMaxTries());
        
        Test savedTest = testRepository.save(test);

        QuizDTO quizDTO = quizServices.getQuizById(quiz.getId());

        return new TestDTO(savedTest.getId(), savedTest.getMaxDuration().toMinutes(), savedTest.getMaxTries(), quizDTO);
    }
    
    public TestDTO getTestById(long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find Test with ID " + testId));
        
        QuizDTO quizDTO = quizServices.getQuizById(test.getQuiz().getId());
        
        return new TestDTO(test.getId(), test.getMaxDuration().toMinutes(), test.getMaxTries(), quizDTO);
    }

    public List<TestDTO> getTestsByTeacherId(long teacherId) {
        List<Test> allTests = testRepository.findAll();
        List<TestDTO> result = new ArrayList<>();

        for (Test test : allTests) {
            if (test.getQuiz().getAuthor().getId().equals(teacherId)) {
                QuizDTO quizDTO = quizServices.getQuizById(test.getQuiz().getId());
                result.add(new TestDTO(test.getId(), test.getMaxDuration().toMinutes(), test.getMaxTries(), quizDTO));
            }
        }
        return result;
    }
}