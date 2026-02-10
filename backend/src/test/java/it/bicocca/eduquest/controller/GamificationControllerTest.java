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
import org.springframework.security.core.Authentication; 
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
    void getAllMissionsSuccess() throws Exception {
        when(missionsServices.getAllMissions()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/gamification/missions/"))
                .andExpect(status().isOk());
        
        verify(missionsServices).getAllMissions();
    }

    @Test
    void getAllMissionsException() throws Exception {
        when(missionsServices.getAllMissions()).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/gamification/missions/"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllMissionsProgressesSuccess() throws Exception {
        when(gamificationServices.getAllMissionsProgressesByUserId(1L, false))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/gamification/missions/progresses")
                        .principal(auth)) 
                .andExpect(status().isOk());
    }

    @Test
    void getAllMissionsProgressesException() throws Exception {
        when(gamificationServices.getAllMissionsProgressesByUserId(anyLong(), anyBoolean()))
                .thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/gamification/missions/progresses")
                        .principal(auth)) 
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getMissionsByUserIdDefaultParam() throws Exception {
        Long targetId = 5L;
        when(gamificationServices.getAllMissionsProgressesByUserId(targetId, false))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/gamification/missions/progresses/{userId}", targetId)
                        .principal(auth))
                .andExpect(status().isOk());
    }

    @Test
    void getMissionsByUserIdWithParam() throws Exception {
        Long targetId = 5L;
        when(gamificationServices.getAllMissionsProgressesByUserId(targetId, true))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/gamification/missions/progresses/{userId}", targetId)
                        .param("onlyCompleted", "true")
                        .principal(auth))
                .andExpect(status().isOk());
        
        verify(gamificationServices).getAllMissionsProgressesByUserId(targetId, true);
    }

    @Test
    void getMissionsByUserIdException() throws Exception {
        when(gamificationServices.getAllMissionsProgressesByUserId(anyLong(), anyBoolean()))
                .thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/gamification/missions/progresses/{userId}", 1L)
                        .principal(auth))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getStudentBadgesSuccess() throws Exception {
        when(gamificationServices.getStudentBadges(1L)).thenReturn(Collections.emptyList());
        
        mockMvc.perform(get("/api/gamification/badges/{userId}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getStudentBadgesException() throws Exception {
        when(gamificationServices.getStudentBadges(1L)).thenThrow(new RuntimeException());
        
        mockMvc.perform(get("/api/gamification/badges/{userId}", 1L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getRankingByNumberOfQuizzesCompletedSuccess() throws Exception {
        when(rankingServices.getRankingByNumberOfQuizzesCompleted())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/gamification/ranking/quizzesCompleted")
                        .principal(auth))
                .andExpect(status().isOk());
    }

    @Test
    void getRankingByNumberOfQuizzesCompletedException() throws Exception {
        when(rankingServices.getRankingByNumberOfQuizzesCompleted())
                .thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/gamification/ranking/quizzesCompleted")
                        .principal(auth))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getRankingByAverageScoreSuccess() throws Exception {
        when(rankingServices.getRankingByAverageQuizzesScore())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/gamification/ranking/averageScore")
                        .principal(auth))
                .andExpect(status().isOk());
    }

    @Test
    void getRankingByAverageScoreException() throws Exception {
        when(rankingServices.getRankingByAverageQuizzesScore())
                .thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/gamification/ranking/averageScore")
                        .principal(auth))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getChallengesByUserIdSuccess() throws Exception {
        when(challengeServices.getChallengesByUserId(1L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/gamification/challenges")
                        .principal(auth)) 
                .andExpect(status().isOk());
    }

    @Test
    void getChallengesByUserIdRuntimeException() throws Exception {
        when(challengeServices.getChallengesByUserId(1L))
                .thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/gamification/challenges")
                        .principal(auth)) 
                .andExpect(status().isBadRequest());
    }

    @Test
    void createChallengeSuccess() throws Exception {
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
    }

    @Test
    void createChallengeException() throws Exception {
        ChallengeCreateDTO dto = new ChallengeCreateDTO();
        when(challengeServices.createChallenge(eq(1L), any(ChallengeCreateDTO.class)))
                .thenThrow(new RuntimeException());

        mockMvc.perform(post("/api/gamification/challenges")
                        .principal(auth) 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void forceExpiryCheckSuccess() throws Exception {
        mockMvc.perform(post("/api/gamification/challenges/refresh-status"))
                .andExpect(status().isOk());

        verify(challengeServices).markExpiredChallenges();
    }

    @Test
    void forceExpiryCheckException() throws Exception {
        doThrow(new RuntimeException()).when(challengeServices).markExpiredChallenges();

        mockMvc.perform(post("/api/gamification/challenges/refresh-status"))
                .andExpect(status().isInternalServerError());
    }
}