package it.bicocca.eduquest.services.listeners;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor; 

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import it.bicocca.eduquest.domain.answers.QuizAttempt;
import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.events.QuizCompletedEvent;
import it.bicocca.eduquest.domain.gamification.Challenge;
import it.bicocca.eduquest.domain.gamification.ChallengeStatus;
import it.bicocca.eduquest.repository.ChallengeRepository;

@ExtendWith(MockitoExtension.class) 
class ChallengeListenerTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeListener listener;

    @Test
    void shouldDoNothing_WhenNoActiveChallengeFound() {
        QuizCompletedEvent event = createEvent(1L, 11L, 88);
        when(challengeRepository.findActiveChallengeForUserAndQuiz(1L, 11L))
            .thenReturn(Collections.emptyList());

        listener.handleChallengeUpdate(event);

        verify(challengeRepository, never()).save(any());
    }

    @Test
    void shouldExpireChallenge_WhenDateIsPast() {
        QuizCompletedEvent event = createEvent(1L, 11L, 88);
        
        Challenge challenge = createBaseChallenge(1L, 2L);
        challenge.setExpiresAt(LocalDateTime.now().minusDays(1)); 

        when(challengeRepository.findActiveChallengeForUserAndQuiz(1L, 11L))
            .thenReturn(List.of(challenge));

        listener.handleChallengeUpdate(event);

        ArgumentCaptor<Challenge> challengeCaptor = ArgumentCaptor.forClass(Challenge.class);
        verify(challengeRepository).save(challengeCaptor.capture());
        
        Challenge savedChallenge = challengeCaptor.getValue();
        assertEquals(ChallengeStatus.EXPIRED, savedChallenge.getStatus(), "Status EXPIRED");
    }

    @Test
    void shouldUpdateChallengerAttempt_WhenChallengerPlaysFirst() {
    	
        Long challengerId = 1L;
        Long opponentId = 2L;
        QuizCompletedEvent event = createEvent(challengerId, 11L, 88);

        Challenge challenge = createBaseChallenge(challengerId, opponentId);
        challenge.setExpiresAt(LocalDateTime.now().plusDays(1)); 

        when(challengeRepository.findActiveChallengeForUserAndQuiz(challengerId, 11L))
            .thenReturn(List.of(challenge));

        listener.handleChallengeUpdate(event);

        verify(challengeRepository).save(challenge);
        assertNotNull(challenge.getChallengerAttempt(), "Save attempt");
        assertNull(challenge.getOpponentAttempt(), "Opponent turn");
        assertNotEquals(ChallengeStatus.COMPLETED, challenge.getStatus(), "Open challenge");
    }

    @Test
    void shouldCloseChallenge_AndSetWinner_WhenBothPlayed() {
        Long challengerId = 1L;
        Long opponentId = 2L;

        QuizCompletedEvent event = createEvent(opponentId, 11L, 50);

        Challenge challenge = createBaseChallenge(challengerId, opponentId);
        challenge.setExpiresAt(LocalDateTime.now().plusDays(1));

        QuizAttempt prevAttempt = new QuizAttempt();
        prevAttempt.setScore(88);
        challenge.setChallengerAttempt(prevAttempt);

        when(challengeRepository.findActiveChallengeForUserAndQuiz(opponentId, 11L))
            .thenReturn(List.of(challenge));

        listener.handleChallengeUpdate(event);

        verify(challengeRepository).save(challenge);
        
        assertEquals(ChallengeStatus.COMPLETED, challenge.getStatus());
        assertNotNull(challenge.getCompletedAt());
        assertNotNull(challenge.getOpponentAttempt(), "2 attempts");
        
        assertEquals(challengerId, challenge.getWinner().getId(), "Challenger win");
    }

    @Test
    void shouldSetNoWinner_WhenScoresAreEqual() {
        Long challengerId = 1L;
        Long opponentId = 2L;

        QuizCompletedEvent event = createEvent(opponentId, 11L, 88);

        Challenge challenge = createBaseChallenge(challengerId, opponentId);
        challenge.setExpiresAt(LocalDateTime.now().plusDays(1));

        QuizAttempt prevAttempt = new QuizAttempt();
        prevAttempt.setScore(88);
        challenge.setChallengerAttempt(prevAttempt);

        when(challengeRepository.findActiveChallengeForUserAndQuiz(opponentId, 11L))
            .thenReturn(List.of(challenge));

        listener.handleChallengeUpdate(event);

        verify(challengeRepository).save(challenge);
        assertEquals(ChallengeStatus.COMPLETED, challenge.getStatus());
        assertNull(challenge.getWinner(), "Draw no winner");
    }

    private QuizCompletedEvent createEvent(Long userId, Long quizId, int score) {
        User user = new User();
        user.setId(userId);
        
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        
        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(user);
        attempt.setQuiz(quiz);
        attempt.setScore(score);
        
        return new QuizCompletedEvent(this, attempt);
    }

    private Challenge createBaseChallenge(Long challengerId, Long opponentId) {
        User a = new User(); a.setId(challengerId);
        User s = new User(); s.setId(opponentId);
        
        Challenge ch = new Challenge();
        ch.setChallenger(a);
        ch.setOpponent(s);
        ch.setStatus(ChallengeStatus.ACTIVE); 
        return ch;
    }
}