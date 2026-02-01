package it.bicocca.eduquest.services.listeners;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import it.bicocca.eduquest.domain.answers.Answer;
import it.bicocca.eduquest.domain.answers.QuizAttempt;
import it.bicocca.eduquest.domain.events.QuizCompletedEvent;
import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.domain.users.StudentStats;
import it.bicocca.eduquest.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
class StudentStatsListenerTest {
	@Mock
	private UsersRepository usersRepository;

	private StudentStatsListener studentStatsListener;
	@BeforeEach
	void setUp() {
		studentStatsListener = new StudentStatsListener(usersRepository);
	}
	
	@Test
	void testHandleUserStatsUpdate_StandardSuccess() {
		Student student = new Student();
		ReflectionTestUtils.setField(student, "id", 1L);
		StudentStats initialStats = new StudentStats(); 
        student.setStats(initialStats);
		
		QuizAttempt attempt = new QuizAttempt();
		attempt.setStudent(student);
		attempt.setScore(11);
		
		Answer correctAns = mock(Answer.class);
        when(correctAns.isCorrect()).thenReturn(true);

        Answer wrongAns = mock(Answer.class);
        when(wrongAns.isCorrect()).thenReturn(false);

        attempt.setAnswers(Arrays.asList(correctAns, wrongAns));
        
        QuizCompletedEvent event = new QuizCompletedEvent(this, attempt);

        studentStatsListener.handleUserStatsUpdate(event);

        verify(usersRepository).save(student);

        assertNotNull(student.getStats());
	}
	
	@Test
    void testHandleUserStatsUpdate_NullStats_CreatedNew() {
        Student student = new Student();
        student.setStats(null); 

        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(student);
        attempt.setScore(5);
        attempt.setAnswers(new ArrayList<>()); 

        QuizCompletedEvent event = new QuizCompletedEvent(this, attempt);

        studentStatsListener.handleUserStatsUpdate(event);

        verify(usersRepository).save(student);
        assertNotNull(student.getStats());
    }

    @Test
    void testHandleUserStatsUpdate_NegativeScore_And_NullAnswers() {
        
        Student student = new Student();
        student.setStats(new StudentStats());

        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(student);
        attempt.setScore(-5); 
        attempt.setAnswers(null);

        QuizCompletedEvent event = new QuizCompletedEvent(this, attempt);

        studentStatsListener.handleUserStatsUpdate(event);

        verify(usersRepository).save(student);
        
    }
}