package it.bicocca.eduquest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.bicocca.eduquest.domain.gamification.ChallengeStatus;
import it.bicocca.eduquest.dto.gamification.ChallengeCreateDTO;
import it.bicocca.eduquest.dto.gamification.ChallengeDTO;
import it.bicocca.eduquest.services.ChallengeServices;
import it.bicocca.eduquest.services.GamificationServices;
import it.bicocca.eduquest.services.MissionsServices;
import it.bicocca.eduquest.services.RankingServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication; // IMPORT IMPORTANTE
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GamificationControllerTest {

    private MockMvc mockMvc;

    @Mock private MissionsServices missionsServices;
    @Mock private GamificationServices gamificationServices;
    @Mock private RankingServices rankingServices;
    @Mock private ChallengeServices challengeServices;
    
    @Mock private Authentication auth; 

    @InjectMocks
    private GamificationController gamificationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(gamificationController).build();
        
        lenient().when(auth.getName()).thenReturn("1");
    }

    @Test
    void getAllMissions() throws Exception {
        when(missionsServices.getAllMissions()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/gamification/missions/"))
                .andExpect(status().isOk());
        
        verify(missionsServices).getAllMissions();
    }

    @Test
    void getAllMissionsProgresses() throws Exception {
        when(gamificationServices.getAllMissionsProgressesByUserId(1L, false))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/gamification/missions/progresses")
                        .principal(auth)) 
                .andExpect(status().isOk());

        verify(gamificationServices).getAllMissionsProgressesByUserId(1L, false);
    }

    @Test
    void getMissionsByUserId() throws Exception {
        Long targetId = 5L;
        when(gamificationServices.getAllMissionsProgressesByUserId(eq(targetId), anyBoolean()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/gamification/missions/progresses/{userId}", targetId)
                        .principal(auth))
                .andExpect(status().isOk());
    }

    @Test
    void getRankingByNumberOfQuizzesCompleted() throws Exception {
        when(rankingServices.getRankingByNumberOfQuizzesCompleted())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/gamification/ranking/quizzesCompleted")
                        .principal(auth))
                .andExpect(status().isOk());
        
        verify(rankingServices).getRankingByNumberOfQuizzesCompleted();
    }

    @Test
    void getRankingByAverageScore() throws Exception {
        when(rankingServices.getRankingByAverageQuizzesScore())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/gamification/ranking/averageScore")
                        .principal(auth))
                .andExpect(status().isOk());

        verify(rankingServices).getRankingByAverageQuizzesScore();
    }

    @Test
    void getChallengesByUserId() throws Exception {
        when(challengeServices.getChallengesByUserId(1L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/gamification/challenges")
                        .principal(auth)) 
                .andExpect(status().isOk());

        verify(challengeServices).getChallengesByUserId(1L);
    }

    @Test
    void createChallenge() throws Exception {
        ChallengeCreateDTO dto = new ChallengeCreateDTO();
        dto.setOpponentId(2L);
        dto.setQuizId(11L);
        
        ChallengeDTO responseDTO = new ChallengeDTO();
        responseDTO.setStatus(ChallengeStatus.ACTIVE);

        when(challengeServices.createChallenge(eq(1L), any(ChallengeCreateDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/gamification/challenges")
                        .principal(auth) 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(challengeServices).createChallenge(eq(1L), any(ChallengeCreateDTO.class));
    }

    @Test
    void forceExpiryCheck() throws Exception {
        mockMvc.perform(post("/api/gamification/challenges/refresh-status"))
                .andExpect(status().isOk());

        verify(challengeServices).markExpiredChallenges();
    }
}