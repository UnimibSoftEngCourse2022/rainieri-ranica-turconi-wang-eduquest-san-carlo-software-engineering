package it.bicocca.eduquest.dto.gamification;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import it.bicocca.eduquest.domain.gamification.ChallengeStatus;

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
        ChallengeDTO dto = new ChallengeDTO(
            1L, 
            2L, "Mario", "Rossi",
            3L, "Luigi", "Verdi",
            "Math Quiz", 
            ChallengeStatus.COMPLETED, 
            now, 
            "Mario", "Rossi"
        );

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getChallengerId());
        assertEquals("Mario", dto.getChallengerName());
        assertEquals("Rossi", dto.getChallengerSurname());
        assertEquals(3L, dto.getOpponentId());
        assertEquals("Luigi", dto.getOpponentName());
        assertEquals("Verdi", dto.getOpponentSurname());
        assertEquals("Math Quiz", dto.getQuizTitle());
        assertEquals(ChallengeStatus.COMPLETED, dto.getStatus());
        assertEquals(now, dto.getExpiresAt());
        assertEquals("Mario", dto.getWinnerName());
        assertEquals("Rossi", dto.getWinnerSurname());
    }

    @Test
    void testSettersAndGetters() {
        ChallengeDTO dto = new ChallengeDTO();
        LocalDateTime expiry = LocalDateTime.now().plusDays(1);

        dto.setId(10L);
        dto.setChallengerId(20L);
        dto.setChallengerName("Alice");
        dto.setChallengerSurname("Smith");
        dto.setOpponentId(30L);
        dto.setOpponentName("Bob");
        dto.setOpponentSurname("Jones");
        dto.setQuizTitle("History");
        
        dto.setStatus(ChallengeStatus.COMPLETED); 
        
        dto.setExpiresAt(expiry);
        dto.setWinnerName("Alice");
        dto.setWinnerSurname("Smith");

        assertEquals(10L, dto.getId());
        assertEquals(20L, dto.getChallengerId());
        assertEquals("Alice", dto.getChallengerName());
        assertEquals("Smith", dto.getChallengerSurname());
        assertEquals(30L, dto.getOpponentId());
        assertEquals("Bob", dto.getOpponentName());
        assertEquals("Jones", dto.getOpponentSurname());
        assertEquals("History", dto.getQuizTitle());
        
        assertEquals(ChallengeStatus.COMPLETED, dto.getStatus());
        
        assertEquals(expiry, dto.getExpiresAt());
        assertEquals("Alice", dto.getWinnerName());
        assertEquals("Smith", dto.getWinnerSurname());
    }
}