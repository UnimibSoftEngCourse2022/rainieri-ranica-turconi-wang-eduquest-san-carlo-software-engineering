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

import it.bicocca.eduquest.dto.quiz.QuizAddDTO;
import it.bicocca.eduquest.dto.quiz.QuizDTO;
import it.bicocca.eduquest.dto.quiz.QuizEditDTO;
import it.bicocca.eduquest.security.JwtUtils; 
import it.bicocca.eduquest.services.QuizServices;

@WebMvcTest(QuizController.class)
class QuizControllerTest {

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
        mockMvc.perform(get("/api/quizzes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1")
    void shouldGetQuizzesByAuthorId() throws Exception {
        mockMvc.perform(get("/api/quizzes")
                .param("authorId", "5")) 
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1")
    void shouldGetQuizById_Success() throws Exception {
        QuizDTO mockQuiz = new QuizDTO(1L, "Title test", "Description", 1L, Collections.emptyList(), null, null);
        
        when(quizService.getQuizById(1L)).thenReturn(mockQuiz);

        mockMvc.perform(get("/api/quizzes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title test")); 
    }

    @Test
    @WithMockUser(username = "1")
    void shouldReturn404_WhenQuizNotFound() throws Exception {
        when(quizService.getQuizById(999L)).thenThrow(new RuntimeException("Cannot find quiz with ID 999"));

        mockMvc.perform(get("/api/quizzes/999"))
                .andExpect(status().isNotFound()); 
    }

    @Test
    @WithMockUser(username = "11") 
    void shouldAddQuiz() throws Exception {
        QuizAddDTO quizDTO = new QuizAddDTO();
        quizDTO.setTitle("New Quiz");
        quizDTO.setDescription("Description test");

        mockMvc.perform(post("/api/quizzes")
                .with(csrf()) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quizDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "11")
    void shouldEditQuiz_Success() throws Exception {
        QuizEditDTO editDTO = new QuizEditDTO("Title modified", "New desc");

        mockMvc.perform(put("/api/quizzes/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "11")
    void shouldReturn403_WhenEditQuizFailsAuthorization() throws Exception {
        QuizEditDTO editDTO = new QuizEditDTO("Title", "Desc");
        
        doThrow(new RuntimeException("You cannot edit quiz from another author!"))
            .when(quizService).editQuiz(anyLong(), any(QuizEditDTO.class), anyLong());

        mockMvc.perform(put("/api/quizzes/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDTO)))
                .andExpect(status().isForbidden()); 
    }

    @Test
    @WithMockUser(username = "11")
    void shouldAddQuestionToQuiz() throws Exception {
        mockMvc.perform(post("/api/quizzes/1/questions/5")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "100")
    void shouldRemoveQuestionFromQuiz() throws Exception {
        mockMvc.perform(delete("/api/quizzes/1/questions/5")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "11")
    void shouldGetQuizForStudent() throws Exception {
        mockMvc.perform(get("/api/quizzes/1/quiz-for-student"))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(username = "1")
    void shouldReturn500_WhenGetQuizzesFails() throws Exception {
        when(quizService.getAllQuizzes()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/quizzes"))
                .andExpect(status().isInternalServerError());
    }
    
    @Test
    @WithMockUser(username = "1")
    void shouldReturn400_WhenAddQuizFails() throws Exception {
        QuizAddDTO quizDTO = new QuizAddDTO(); 
        when(quizService.addQuiz(any(), anyLong())).thenThrow(new RuntimeException("Invalid data"));

        mockMvc.perform(post("/api/quizzes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quizDTO)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(username = "1")
    void shouldReturn404_WhenEditQuizNotFound() throws Exception {
        QuizEditDTO editDTO = new QuizEditDTO("T", "D");
        // Simula "Cannot find" per entrare nel primo IF del catch
        doThrow(new RuntimeException("Cannot find quiz"))
            .when(quizService).editQuiz(anyLong(), any(), anyLong());

        mockMvc.perform(put("/api/quizzes/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "1")
    void shouldReturn400_WhenEditQuizIllegalArgument() throws Exception {
        QuizEditDTO editDTO = new QuizEditDTO("T", "D");
        doThrow(new IllegalArgumentException("Bad argument"))
            .when(quizService).editQuiz(anyLong(), any(), anyLong());

        mockMvc.perform(put("/api/quizzes/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDTO)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(username = "1")
    void shouldReturn404_WhenAddQuestionQuizNotFound() throws Exception {
        doThrow(new RuntimeException("Cannot find quiz"))
            .when(quizService).addQuestionToQuiz(anyLong(), anyLong(), anyLong());

        mockMvc.perform(post("/api/quizzes/1/questions/5").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "1")
    void shouldReturn403_WhenAddQuestionForbidden() throws Exception {
        doThrow(new RuntimeException("cannot edit quiz"))
            .when(quizService).addQuestionToQuiz(anyLong(), anyLong(), anyLong());

        mockMvc.perform(post("/api/quizzes/1/questions/5").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1")
    void shouldReturn400_WhenAddQuestionGenericError() throws Exception {
        doThrow(new RuntimeException("Generic error"))
            .when(quizService).addQuestionToQuiz(anyLong(), anyLong(), anyLong());

        mockMvc.perform(post("/api/quizzes/1/questions/5").with(csrf()))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(username = "1")
    void shouldReturn404_WhenRemoveQuestionNotFound() throws Exception {
        doThrow(new RuntimeException("Cannot find question"))
            .when(quizService).removeQuestionFromQuiz(anyLong(), anyLong(), anyLong());

        mockMvc.perform(delete("/api/quizzes/1/questions/5").with(csrf()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(username = "1")
    void shouldReturn404_WhenGetStudentQuizNotFound() throws Exception {
        doThrow(new RuntimeException("Cannot find a quiz"))
            .when(quizService).getQuizForStudent(anyLong(), anyLong());

        mockMvc.perform(get("/api/quizzes/1/quiz-for-student"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "1")
    void shouldReturn403_WhenTeacherTriesToTakeQuiz() throws Exception {
        doThrow(new RuntimeException("Teacher cannot take quiz"))
            .when(quizService).getQuizForStudent(anyLong(), anyLong());

        mockMvc.perform(get("/api/quizzes/1/quiz-for-student"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1")
    void shouldReturn500_WhenGetStudentQuizGenericError() throws Exception {
        doThrow(new RuntimeException("Database down"))
            .when(quizService).getQuizForStudent(anyLong(), anyLong());

        mockMvc.perform(get("/api/quizzes/1/quiz-for-student"))
                .andExpect(status().isInternalServerError());
    }
}