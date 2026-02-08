package it.bicocca.eduquest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.bicocca.eduquest.dto.quiz.TestAddDTO;
import it.bicocca.eduquest.dto.quizAttempt.QuizAttemptDTO;
import it.bicocca.eduquest.dto.quiz.TestDTO; 
import it.bicocca.eduquest.security.JwtUtils;
import it.bicocca.eduquest.services.TestServices;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TestServices testServices;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private Authentication auth;

    @InjectMocks
    private TestController testController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(testController).build();
    }

    @Test
    void createTestWhenValid() throws Exception {

    	TestAddDTO testAddDTO = new TestAddDTO();
        
        when(auth.getName()).thenReturn("10");

        when(testServices.createTest(any(TestAddDTO.class), eq(10L)))
            .thenReturn(new TestDTO());

        mockMvc.perform(post("/api/tests")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAddDTO)))
                .andExpect(status().isOk());

        verify(testServices).createTest(any(TestAddDTO.class), eq(10L));
    }

    @Test
    void createTestWhenExceptionOccurs() throws Exception {
        TestAddDTO testAddDTO = new TestAddDTO();
        when(auth.getName()).thenReturn("10");
        
        when(testServices.createTest(any(), anyLong()))
                .thenThrow(new IllegalArgumentException("Not valid"));

        mockMvc.perform(post("/api/tests")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAddDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTestsNoAuthorId() throws Exception {
        when(testServices.getAllTests()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tests"))
                .andExpect(status().isOk());

        verify(testServices).getAllTests();
    }

    @Test
    void getAllTestsAuthorIdProvided() throws Exception {
        Long authorId = 5L;
        when(testServices.getTestsByTeacherId(authorId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tests")
                        .param("authorId", String.valueOf(authorId)))
                .andExpect(status().isOk());

        verify(testServices).getTestsByTeacherId(authorId);
    }

    @Test
    void getTestById() throws Exception {
        Long testId = 1L;
        
        when(testServices.getTestById(testId)).thenReturn(new TestDTO());

        mockMvc.perform(get("/api/tests/{id}", testId))
                .andExpect(status().isOk());

        verify(testServices).getTestById(testId);
    }

    @Test
    void getTestByIdNotFound() throws Exception {
        Long testId = 99L;
        when(testServices.getTestById(testId)).thenThrow(new RuntimeException("Test not found"));

        mockMvc.perform(get("/api/tests/{id}", testId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTest() throws Exception {
        Long testId = 1L;
        doNothing().when(testServices).deleteTest(testId);

        mockMvc.perform(delete("/api/tests/{id}", testId))
                .andExpect(status().isOk());

        verify(testServices).deleteTest(testId);
    }

    @Test
    void getMyAttemptsReturnList() throws Exception {
        Long testId = 10L;
        Long studentId = 123L;
        String token = "Bearer fake-jwt-token";
        String jwtOnly = "fake-jwt-token";

        when(jwtUtils.getUserIdFromToken(jwtOnly)).thenReturn(studentId);

        when(testServices.getAttemptsForStudentAndTest(testId, studentId))
                .thenReturn(List.of(new QuizAttemptDTO()));

        mockMvc.perform(get("/api/tests/{testId}/my-attempts", testId)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        verify(jwtUtils).getUserIdFromToken(jwtOnly);
        verify(testServices).getAttemptsForStudentAndTest(testId, studentId);
    }
}