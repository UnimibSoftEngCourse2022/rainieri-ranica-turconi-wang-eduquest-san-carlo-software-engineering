package it.bicocca.eduquest.services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import it.bicocca.eduquest.domain.answers.QuizAttempt;
import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.quiz.Test;
import it.bicocca.eduquest.domain.users.Teacher;
import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.dto.quiz.QuizDTO;
import it.bicocca.eduquest.dto.quiz.TestAddDTO;
import it.bicocca.eduquest.dto.quiz.TestDTO;
import it.bicocca.eduquest.dto.quizAttempt.QuizAttemptDTO;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.repository.QuizAttemptsRepository; 
import it.bicocca.eduquest.repository.TestRepository;
import it.bicocca.eduquest.repository.UsersRepository;

@Service
public class TestServices {

    private final TestRepository testRepository;
    private final QuizRepository quizRepository;
    private final UsersRepository usersRepository;
    private final QuizServices quizServices;
    private final QuizAttemptsRepository quizAttemptsRepository; 

    public TestServices(TestRepository testRepository, QuizRepository quizRepository, 
                        UsersRepository usersRepository, QuizServices quizServices,
                        QuizAttemptsRepository quizAttemptsRepository) {
        this.testRepository = testRepository;
        this.quizRepository = quizRepository;
        this.usersRepository = usersRepository;
        this.quizServices = quizServices;
        this.quizAttemptsRepository = quizAttemptsRepository;
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
        if (dto.getMaxDurationMinutes() != null && dto.getMaxDurationMinutes() > 0) {
            duration = Duration.ofMinutes(dto.getMaxDurationMinutes());
        }

        Test test = new Test();
        test.setQuiz(quiz);
        test.setMaxDuration(duration);
        test.setMaxTries(dto.getMaxTries());
        
        Test savedTest = testRepository.save(test);

        return convertToDTO(savedTest);
    }
    
    public TestDTO getTestById(long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find Test with ID " + testId));
        
        return convertToDTO(test);
    }

    public List<TestDTO> getTestsByTeacherId(long teacherId) {
        List<Test> allTests = testRepository.findAll();
        List<TestDTO> result = new ArrayList<>();

        for (Test test : allTests) {
            if (test.getQuiz().getAuthor().getId().equals(teacherId)) {
                result.add(convertToDTO(test));
            }
        }
        return result;
    }
    
	 public List<QuizAttemptDTO> getAttemptsForStudentAndTest(Long testId, Long studentId) {
	    
	     List<QuizAttempt> attempts = quizAttemptsRepository.findByStudentIdAndTestId(studentId, testId);
	     
	     List<QuizAttemptDTO> dtos = new ArrayList<>();
	     for (QuizAttempt attempt : attempts) {
	         QuizAttemptDTO dto = new QuizAttemptDTO();
	         dto.setId(attempt.getId());
	         dto.setScore(attempt.getScore());
	         dto.setFinishedAt(attempt.getFinishedAt()); 
	         dtos.add(dto);
	     }
	     return dtos;
	 }

    public List<TestDTO> getAllTests() {
        List<Test> allTests = testRepository.findAll();
        List<TestDTO> result = new ArrayList<>();

        for (Test test : allTests) {
            result.add(convertToDTO(test));
        }
        return result;
    }

    private TestDTO convertToDTO(Test test) {
        QuizDTO quizDTO = quizServices.getQuizById(test.getQuiz().getId());
        
        long durationMinutes = 0;
        if (test.getMaxDuration() != null) {
            durationMinutes = test.getMaxDuration().toMinutes();
        }
        
        Double avg = quizAttemptsRepository.getAverageScoreByTest(test);
        long totalAttempts = quizAttemptsRepository.countByTest(test);

        TestDTO dto = new TestDTO(test.getId(), durationMinutes, test.getMaxTries(), quizDTO);
        
        dto.setTestAverageScore(avg);
        dto.setTestTotalAttempts((int) totalAttempts);
        
        return dto;
    }
    
    public void deleteTest(long testId) {
        if (!testRepository.existsById(testId)) {
            throw new IllegalArgumentException("Test with ID " + testId + " not found.");
        }
        testRepository.deleteById(testId);
    }
}