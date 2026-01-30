package it.bicocca.eduquest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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

    // --- START QUIZ TESTS ---

    @Test
    @WithMockUser(username = "1") // L'utente loggato ha ID 1
    void startQuiz_Success() throws Exception {
        QuizSessionDTO sessionDTO = new QuizSessionDTO();
        // Il service viene chiamato con quizId=10 e studentId=1
        when(quizAttemptServices.startQuiz(10L, 1L)).thenReturn(sessionDTO);

        mockMvc.perform(post("/api/quizAttempt/start")
                .param("quizId", "10")
                .param("studentId", "1") // Deve corrispondere all'utente loggato (1)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1") // Utente loggato: 1
    void startQuiz_Forbidden_WrongUser() throws Exception {
        // Provo a iniziare un quiz per l'utente 2 (ma io sono l'1)
        mockMvc.perform(post("/api/quizAttempt/start")
                .param("quizId", "10")
                .param("studentId", "2") // ID diverso da logged user -> 403 Forbidden
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(username = "1")
    void startQuiz_NotFound() throws Exception {
        // Il service lancia "Cannot find quiz" -> Controller deve dare 404
        when(quizAttemptServices.startQuiz(99L, 1L))
            .thenThrow(new RuntimeException("Cannot find quiz"));

        mockMvc.perform(post("/api/quizAttempt/start")
                .param("quizId", "99")
                .param("studentId", "1")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // --- SAVE ANSWER TESTS ---

    @Test
    @WithMockUser(username = "1") // L'utente loggato ha ID 1
    void saveSingleAnswer_Success() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        
        // Il service ora riceve anche l'ID utente (1L) come secondo parametro
        when(quizAttemptServices.saveSingleAnswer(any(AnswerDTO.class), eq(1L)))
            .thenReturn(answerDTO);

        mockMvc.perform(put("/api/quizAttempt/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answerDTO))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1")
    void saveSingleAnswer_Forbidden_NotYourAttempt() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        
        // Simuliamo che il service lanci l'eccezione di sicurezza
        when(quizAttemptServices.saveSingleAnswer(any(AnswerDTO.class), eq(1L)))
            .thenThrow(new RuntimeException("This is not your attempt!"));

        mockMvc.perform(put("/api/quizAttempt/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answerDTO))
                .with(csrf()))
                .andExpect(status().isForbidden()); // Ci aspettiamo 403
    }

    @Test
    @WithMockUser(username = "1")
    void saveSingleAnswer_BadRequest() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        // Errore generico (es. validazione) -> 400 Bad Request
        when(quizAttemptServices.saveSingleAnswer(any(AnswerDTO.class), eq(1L)))
            .thenThrow(new RuntimeException("Invalid answer data"));

        mockMvc.perform(put("/api/quizAttempt/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answerDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    // --- COMPLETE QUIZ TESTS ---

    @Test
    @WithMockUser(username = "1") // L'utente loggato ha ID 1
    void completeQuizAttempt_Success() throws Exception {
        QuizAttemptDTO result = new QuizAttemptDTO();
        // Il service riceve (attemptId, userId)
        when(quizAttemptServices.completeQuizAttempt(100L, 1L)).thenReturn(result);

        mockMvc.perform(post("/api/quizAttempt/100/complete")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1")
    void completeQuizAttempt_Forbidden() throws Exception {
        // Simuliamo tentativo di chiudere il quiz di un altro
        when(quizAttemptServices.completeQuizAttempt(100L, 1L))
            .thenThrow(new RuntimeException("not your attempt"));

        mockMvc.perform(post("/api/quizAttempt/100/complete")
                .with(csrf()))
                .andExpect(status().isForbidden()); // 403
    }

    @Test
    @WithMockUser(username = "1")
    void completeQuizAttempt_BadRequest() throws Exception {
        when(quizAttemptServices.completeQuizAttempt(100L, 1L))
            .thenThrow(new RuntimeException("Quiz already completed"));

        mockMvc.perform(post("/api/quizAttempt/100/complete")
                .with(csrf()))
                .andExpect(status().isBadRequest()); // 400
    }

    @Test
    @WithMockUser(username = "1")
    void completeQuizAttempt_InternalServerError() throws Exception {
        // NullPointer o altre eccezioni non gestite -> 500
        when(quizAttemptServices.completeQuizAttempt(100L, 1L))
            .thenThrow(new NullPointerException("Unexpected error"));

        mockMvc.perform(post("/api/quizAttempt/100/complete")
                .with(csrf()))
                .andExpect(status().isInternalServerError()); // 500
    }
}