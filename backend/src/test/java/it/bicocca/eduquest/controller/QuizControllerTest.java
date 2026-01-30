package it.bicocca.eduquest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.bicocca.eduquest.dto.quiz.QuestionAddDTO;
import it.bicocca.eduquest.dto.quiz.QuestionType;
import it.bicocca.eduquest.dto.quiz.QuizAddDTO;
import it.bicocca.eduquest.dto.quiz.QuizDTO;
import it.bicocca.eduquest.dto.quiz.QuizEditDTO;
import it.bicocca.eduquest.security.JwtUtils; 
import it.bicocca.eduquest.services.QuizServices;

@WebMvcTest(QuizController.class)
public class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @MockitoBean
    private QuizServices quizService;

    @MockitoBean
    private JwtUtils jwtUtils; 

    @Test
    @WithMockUser(username = "1") 
    void shouldGetAllQuizzes() throws Exception {
        mockMvc.perform(get("/api/quiz"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1")
    void shouldGetQuizzesByAuthorId() throws Exception {
        mockMvc.perform(get("/api/quiz")
                .param("authorId", "5")) 
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1")
    void shouldGetQuizById_Success() throws Exception {
        QuizDTO mockQuiz = new QuizDTO(1L, "Titolo Test", "Descrizione", 1L, Collections.emptyList());
        
        when(quizService.getQuizById(1L)).thenReturn(mockQuiz);

        mockMvc.perform(get("/api/quiz/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Titolo Test")); 
    }

    @Test
    @WithMockUser(username = "1")
    void shouldReturn404_WhenQuizNotFound() throws Exception {
        when(quizService.getQuizById(999L)).thenThrow(new RuntimeException("Cannot find quiz with ID 999"));

        mockMvc.perform(get("/api/quiz/999"))
                .andExpect(status().isNotFound()); 
    }

    @Test
    @WithMockUser(username = "11") 
    void shouldAddQuiz() throws Exception {
        QuizAddDTO quizDTO = new QuizAddDTO();
        quizDTO.setTitle("Nuovo Quiz");
        quizDTO.setDescription("Descrizione prova");

        mockMvc.perform(post("/api/quiz")
                .with(csrf()) // OBBLIGATORIO per le POST nei test
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quizDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "11")
    void shouldAddQuestion() throws Exception {
        QuestionAddDTO questionDTO = new QuestionAddDTO();
        questionDTO.setText("Domanda di prova?");
        questionDTO.setTopic("Generale");
        questionDTO.setQuestionType(QuestionType.OPENED); 

        mockMvc.perform(post("/api/quiz/question")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questionDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "11")
    void shouldEditQuiz_Success() throws Exception {
        QuizEditDTO editDTO = new QuizEditDTO("Titolo Modificato", "Nuova desc");

        mockMvc.perform(put("/api/quiz/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "11")
    void shouldReturn403_WhenEditQuizFailsAuthorization() throws Exception {
        QuizEditDTO editDTO = new QuizEditDTO("Titolo", "Desc");
        
        doThrow(new RuntimeException("You cannot edit quiz from another author!"))
            .when(quizService).editQuiz(anyLong(), any(QuizEditDTO.class), anyLong());

        mockMvc.perform(put("/api/quiz/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDTO)))
                .andExpect(status().isForbidden()); 
    }

    @Test
    @WithMockUser(username = "11")
    void shouldAddQuestionToQuiz() throws Exception {
        mockMvc.perform(post("/api/quiz/1/add-question/5")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "100")
    void shouldRemoveQuestionFromQuiz() throws Exception {
        mockMvc.perform(delete("/api/quiz/1/remove-question/5")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "100")
    void shouldGetQuizForStudent() throws Exception {
        mockMvc.perform(get("/api/quiz/1/quiz-for-student"))
                .andExpect(status().isOk());
    }
}