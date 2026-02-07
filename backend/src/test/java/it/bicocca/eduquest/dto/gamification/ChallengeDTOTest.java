package it.bicocca.eduquest.dto.gamification;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import it.bicocca.eduquest.domain.gamification.ChallengeStatus;
import it.bicocca.eduquest.dto.user.ChallengeUserDTO;

class ChallengeDTOTest {

    @Test
    void testNoArgsConstructor() {
        ChallengeDTO dto = new ChallengeDTO();
        assertNotNull(dto);
        assertEquals(ChallengeStatus.ACTIVE, dto.getStatus());
        assertNull(dto.getId());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        
        ChallengeUserDTO challenger = new ChallengeUserDTO(2L, "Mario", "Rossi", false);
        ChallengeUserDTO opponent = new ChallengeUserDTO(3L, "Luigi", "Verdi", false);
        
        ChallengeDTO dto = new ChallengeDTO(
            1L, 
            challenger,
            opponent,
            "Math Quiz", 
            ChallengeStatus.COMPLETED, 
            now, 
            "Mario", "Rossi"
        );

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getChallenger().getId());
        assertEquals("Mario", dto.getChallenger().getName());
        assertEquals("Rossi", dto.getChallenger().getSurname());
        assertEquals(3L, dto.getOpponent().getId());
        assertEquals("Luigi", dto.getOpponent().getName());
        assertEquals("Verdi", dto.getOpponent().getSurname());
        assertEquals("Math Quiz", dto.getQuizTitle());
        assertEquals(ChallengeStatus.COMPLETED, dto.getStatus());
        assertEquals(now, dto.getExpiresAt());
        assertEquals("Mario", dto.getWinnerName());
        assertEquals("Rossi", dto.getWinnerSurname());
        assertEquals(false, challenger.isHasCompletedQuiz());
        assertEquals(false, opponent.isHasCompletedQuiz());
    }

    @Test
    void testSettersAndGetters() {
        ChallengeDTO dto = new ChallengeDTO();
        LocalDateTime expiry = LocalDateTime.now().plusDays(1);

        ChallengeUserDTO challenger = new ChallengeUserDTO(20L, "Alice", "Smith", false);
        ChallengeUserDTO opponent = new ChallengeUserDTO(30L, "Bob", "Jones", false);
        
        dto.setId(10L);
        dto.setChallenger(challenger);
        dto.setOpponent(opponent);
        dto.setQuizTitle("History");
        
        dto.setStatus(ChallengeStatus.COMPLETED);
        
        dto.setExpiresAt(expiry);
        dto.setWinnerName("Alice");
        dto.setWinnerSurname("Smith");

        assertEquals(10L, dto.getId());
        assertEquals(20L, dto.getChallenger().getId());
        assertEquals("Alice", dto.getChallenger().getName());
        assertEquals("Smith", dto.getChallenger().getSurname());
        assertEquals(30L, dto.getOpponent().getId());
        assertEquals("Bob", dto.getOpponent().getName());
        assertEquals("Jones", dto.getOpponent().getSurname());
        assertEquals("History", dto.getQuizTitle());
        assertEquals(false, challenger.isHasCompletedQuiz());
        assertEquals(false, opponent.isHasCompletedQuiz());
        
        assertEquals(ChallengeStatus.COMPLETED, dto.getStatus());
        
        assertEquals(expiry, dto.getExpiresAt());
        assertEquals("Alice", dto.getWinnerName());
        assertEquals("Smith", dto.getWinnerSurname());
    }
}