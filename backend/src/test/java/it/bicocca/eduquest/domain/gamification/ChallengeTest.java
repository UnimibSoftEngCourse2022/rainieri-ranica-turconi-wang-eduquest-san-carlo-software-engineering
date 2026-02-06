package it.bicocca.eduquest.domain.gamification;

import org.junit.jupiter.api.Test;
import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.domain.quiz.Quiz;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeTest {

    @Test
    void constructor_ShouldCalculateExpirationDateCorrectly() {
        User u1 = new User();
        User u2 = new User();
        Quiz quiz = new Quiz();
        int durationHours = 24;

        Challenge challenge = new Challenge(u1, u2, quiz, durationHours);

        assertNotNull(challenge.getCreatedAt(), "create date not null");
        assertNotNull(challenge.getExpiresAt(), "expirade date not null");
        assertEquals(ChallengeStatus.ACTIVE, challenge.getStatus(), "status ACTIVE");
        
        assertTrue(challenge.getExpiresAt().isAfter(challenge.getCreatedAt()));
        
        assertTrue(challenge.getExpiresAt().isAfter(LocalDateTime.now().plusHours(23)));
    }

    @Test
    void isExpired_ShouldReturnTrue_WhenDateIsPast_AndStatusActive() {
        Challenge challenge = new Challenge();
        challenge.setStatus(ChallengeStatus.ACTIVE);
        
        challenge.setExpiresAt(LocalDateTime.now().minusDays(1)); 

        assertTrue(challenge.isExpired(), "passed expired date");
    }

    @Test
    void isExpired_ShouldReturnFalse_WhenDateIsFuture() {
        Challenge challenge = new Challenge();
        challenge.setStatus(ChallengeStatus.ACTIVE);
        
        challenge.setExpiresAt(LocalDateTime.now().plusDays(1)); 

        assertFalse(challenge.isExpired(), "not expired");
    }

    @Test
    void isExpired_ShouldReturnFalse_WhenStatusIsCompleted_EvenIfDateIsPast() {
        Challenge challenge = new Challenge();
        challenge.setStatus(ChallengeStatus.COMPLETED);
        
        challenge.setExpiresAt(LocalDateTime.now().minusYears(1)); 

        assertFalse(challenge.isExpired(), "completed challenge not expired");
    }
}