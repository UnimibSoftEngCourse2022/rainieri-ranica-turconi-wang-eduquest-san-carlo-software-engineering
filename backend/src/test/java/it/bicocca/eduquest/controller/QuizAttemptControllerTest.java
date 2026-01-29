package it.bicocca.eduquest.controller;

import static org.mockito.ArgumentMatchers.any;
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
    @WithMockUser
    void startQuiz_Success() throws Exception {
        QuizSessionDTO sessionDTO = new QuizSessionDTO();
        when(quizAttemptServices.startQuiz(1L, 2L)).thenReturn(sessionDTO);

        mockMvc.perform(post("/api/quizAttempt/start")
                .param("quizId", "1")
                .param("studentId", "2")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void startQuiz_Unauthorized() throws Exception {
        when(quizAttemptServices.startQuiz(1L, 2L)).thenThrow(new RuntimeException("Unauthorized access"));

        mockMvc.perform(post("/api/quizAttempt/start")
                .param("quizId", "1")
                .param("studentId", "2")
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void saveSingleAnswer_Success() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        when(quizAttemptServices.saveSingleAnswer(any(AnswerDTO.class))).thenReturn(answerDTO);

        mockMvc.perform(put("/api/quizAttempt/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answerDTO))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void saveSingleAnswer_BadRequest() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        when(quizAttemptServices.saveSingleAnswer(any(AnswerDTO.class))).thenThrow(new RuntimeException("Invalid answer"));

        mockMvc.perform(put("/api/quizAttempt/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answerDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void completeQuizAttempt_Success() throws Exception {
        QuizAttemptDTO result = new QuizAttemptDTO();
        when(quizAttemptServices.completeQuizAttempt(1L)).thenReturn(result);

        mockMvc.perform(post("/api/quizAttempt/1/complete")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void completeQuizAttempt_BadRequest() throws Exception {
        when(quizAttemptServices.completeQuizAttempt(1L)).thenThrow(new RuntimeException("Cannot complete quiz"));

        mockMvc.perform(post("/api/quizAttempt/1/complete")
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
	@WithMockUser
	void completeQuizAttempt_InternalServerError() throws Exception {
		when(quizAttemptServices.completeQuizAttempt(1L)).thenThrow(new NullPointerException("Unexpected error"));

		mockMvc.perform(post("/api/quizAttempt/1/complete")
				.with(csrf()))
				.andExpect(status().isBadRequest());
	}
}