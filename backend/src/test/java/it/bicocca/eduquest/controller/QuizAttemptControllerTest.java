package it.bicocca.eduquest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.bicocca.eduquest.dto.quizAttempt.AnswerDTO;
import it.bicocca.eduquest.dto.quizAttempt.QuizAttemptDTO;
import it.bicocca.eduquest.dto.quizAttempt.QuizSessionDTO;
import it.bicocca.eduquest.security.JwtUtils;
import it.bicocca.eduquest.services.QuizAttemptServices;

@WebMvcTest(QuizAttemptController.class)
public class QuizAttemptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private QuizAttemptServices quizAttemptServices;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    @WithMockUser(username = "1") 
    void startQuiz_Success() throws Exception {
        QuizSessionDTO sessionDTO = new QuizSessionDTO();
        when(quizAttemptServices.startQuiz(10L, 1L)).thenReturn(sessionDTO);

        mockMvc.perform(post("/api/quiz-attempts")
                .param("quizId", "10")
                .param("studentId", "1")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1")
    void startQuiz_Forbidden_WrongUser() throws Exception {
        mockMvc.perform(post("/api/quiz-attempts")
                .param("quizId", "10")
                .param("studentId", "2")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(username = "1")
    void startQuiz_NotFound() throws Exception {
        when(quizAttemptServices.startQuiz(99L, 1L))
            .thenThrow(new RuntimeException("Cannot find quiz"));

        mockMvc.perform(post("/api/quiz-attempts")
                .param("quizId", "99")
                .param("studentId", "1")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "1")
    void saveSingleAnswer_Success() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        
        when(quizAttemptServices.saveSingleAnswer(any(AnswerDTO.class), eq(1L)))
            .thenReturn(answerDTO);

        mockMvc.perform(put("/api/quiz-attempts/1/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answerDTO))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1")
    void saveSingleAnswer_Forbidden_NotYourAttempt() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        
        when(quizAttemptServices.saveSingleAnswer(any(AnswerDTO.class), eq(1L)))
            .thenThrow(new RuntimeException("not your attempt"));

        mockMvc.perform(put("/api/quiz-attempts/1/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answerDTO))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1")
    void saveSingleAnswer_BadRequest() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        
        when(quizAttemptServices.saveSingleAnswer(any(AnswerDTO.class), eq(1L)))
            .thenThrow(new RuntimeException("Invalid answer data"));

        mockMvc.perform(put("/api/quiz-attempts/1/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answerDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "1")
    void completeQuizAttempt_Success() throws Exception {
        QuizAttemptDTO result = new QuizAttemptDTO();
        when(quizAttemptServices.completeQuizAttempt(100L, 1L)).thenReturn(result);

        mockMvc.perform(post("/api/quiz-attempts/100/complete")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1")
    void completeQuizAttempt_Forbidden() throws Exception {
        when(quizAttemptServices.completeQuizAttempt(100L, 1L))
            .thenThrow(new RuntimeException("not your attempt"));

        mockMvc.perform(post("/api/quiz-attempts/100/complete")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1")
    void completeQuizAttempt_BadRequest() throws Exception {
        when(quizAttemptServices.completeQuizAttempt(100L, 1L))
            .thenThrow(new RuntimeException("Quiz already completed"));

        mockMvc.perform(post("/api/quiz-attempts/100/complete")
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "1")
    void completeQuizAttempt_InternalServerError() throws Exception {
        when(quizAttemptServices.completeQuizAttempt(100L, 1L))
            .thenThrow(new NullPointerException("Unexpected error"));

        mockMvc.perform(post("/api/quiz-attempts/100/complete")
                .with(csrf()))
                .andExpect(status().isInternalServerError());
    }
}