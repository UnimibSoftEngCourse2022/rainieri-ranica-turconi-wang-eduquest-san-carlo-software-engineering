package it.bicocca.eduquest.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.time.LocalDateTime;

import it.bicocca.eduquest.repository.ChallengeRepository;
import it.bicocca.eduquest.repository.QuizRepository;
import it.bicocca.eduquest.repository.UsersRepository;
import it.bicocca.eduquest.dto.gamification.ChallengeCreateDTO;
import it.bicocca.eduquest.dto.gamification.ChallengeDTO;
import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.domain.quiz.Quiz;
import it.bicocca.eduquest.domain.gamification.Challenge;
import it.bicocca.eduquest.domain.gamification.ChallengeStatus;

@ExtendWith(MockitoExtension.class)
class ChallengeServicesTest {

    @Mock private ChallengeRepository challengeRepository;
    @Mock private QuizRepository quizRepository;
    @Mock private UsersRepository usersRepository;

    @InjectMocks
    private ChallengeServices challengeServices;

    @Test
    void createChallenge_ShouldSaveAndReturnDTO_WhenAllIdsAreValid() {
        Long challengerId = 1L;
        Long opponentId = 2L;
        Long quizId = 11L;

        ChallengeCreateDTO dto = new ChallengeCreateDTO();
        dto.setOpponentId(opponentId);
        dto.setQuizId(quizId);
        dto.setDurationInHours(24);

        User challenger = new User(); challenger.setId(challengerId); challenger.setName("Mario");
        User opponent = new User(); opponent.setId(opponentId); opponent.setName("Luigi");
        Quiz quiz = new Quiz(); quiz.setId(quizId); quiz.setTitle("Math");

        when(usersRepository.findById(challengerId)).thenReturn(Optional.of(challenger));
        when(usersRepository.findById(opponentId)).thenReturn(Optional.of(opponent));
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        ChallengeDTO result = challengeServices.createChallenge(challengerId, dto);

        verify(challengeRepository).save(any(Challenge.class));
        
        assertNotNull(result);
        assertEquals(challengerId, result.getChallengerId());
        assertEquals(opponentId, result.getOpponentId());
        assertEquals("Math", result.getQuizTitle());
        assertEquals(ChallengeStatus.ACTIVE, result.getStatus());
    }

    @Test
    void createChallenge_ShouldThrowException_WhenUserNotFound() {
    	
        Long challengerId = 99L; 
        ChallengeCreateDTO dto = new ChallengeCreateDTO();
        
        when(usersRepository.findById(challengerId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            challengeServices.createChallenge(challengerId, dto);
        });

        assertEquals("Challenger not found", exception.getMessage());
        
        verify(challengeRepository, never()).save(any());
    }

    @Test
    void getChallengesByUserId_ShouldReturnOnlyUserChallenges() {
        Long myId = 1L;
        Long otherId = 2L;
        Long strangerId = 3L;

        Challenge c1 = createChallengeMock(myId, otherId);
        c1.setId(101L);
        
        Challenge c2 = createChallengeMock(otherId, myId);
        c2.setId(102L);

        Challenge c3 = createChallengeMock(otherId, strangerId);
        c3.setId(103L);

        when(challengeRepository.findAll()).thenReturn(List.of(c1, c2, c3));

        List<ChallengeDTO> results = challengeServices.getChallengesByUserId(myId);

        assertEquals(2, results.size(), "Dovrei trovare solo 2 sfide");
        
        assertTrue(results.stream().anyMatch(dto -> dto.getId().equals(101L)));
        assertTrue(results.stream().anyMatch(dto -> dto.getId().equals(102L)));
        assertFalse(results.stream().anyMatch(dto -> dto.getId().equals(103L)));
    }

    @Test
    void markExpiredChallenges_ShouldUpdateStatusAndSave() {
        Challenge c1 = new Challenge();
        c1.setStatus(ChallengeStatus.ACTIVE);
        
        Challenge c2 = new Challenge();
        c2.setStatus(ChallengeStatus.ACTIVE);

        when(challengeRepository.findExpiredActiveChallenges(any(LocalDateTime.class)))
            .thenReturn(List.of(c1, c2));

        challengeServices.markExpiredChallenges();

        verify(challengeRepository, times(2)).save(any(Challenge.class));
        
        assertEquals(ChallengeStatus.EXPIRED, c1.getStatus());
        assertEquals(ChallengeStatus.EXPIRED, c2.getStatus());
    }

    private Challenge createChallengeMock(Long idChallenger, Long idOpponent) {
        User c = new User(); c.setId(idChallenger); c.setName("C"); c.setSurname("S");
        User o = new User(); o.setId(idOpponent); o.setName("O"); o.setSurname("S");
        Quiz q = new Quiz(); q.setId(10L); q.setTitle("Test");
        
        Challenge ch = new Challenge();
        ch.setId((long)(Math.random() * 1000));
        ch.setChallenger(c);
        ch.setOpponent(o);
        ch.setQuiz(q);
        ch.setStatus(ChallengeStatus.ACTIVE);
        return ch;
    }
}