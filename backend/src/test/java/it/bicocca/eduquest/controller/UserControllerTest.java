package it.bicocca.eduquest.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import it.bicocca.eduquest.dto.user.UserInfoDTO;
import it.bicocca.eduquest.services.UserServices;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserServices userServices;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getAllInfos_Success() throws Exception {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userServices.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users")
                .principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    void getAllInfos_Unauthorized_NullAuth() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Access denied, you must be authenticated"));
    }

    @Test
    void getAllInfos_Unauthorized_NotAuthenticated() throws Exception {
        when(authentication.isAuthenticated()).thenReturn(false);

        mockMvc.perform(get("/api/users")
                .principal(authentication))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Access denied, you must be authenticated"));
    }

    @Test
    void getAllInfos_InternalServerError() throws Exception {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userServices.getAllUsers()).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(get("/api/users")
                .principal(authentication))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Errore interno nel recupero utenti: DB Error"));
    }

    @Test
    void getUserInfo_Success() throws Exception {
        UserInfoDTO mockUser = mock(UserInfoDTO.class);
        when(userServices.getUserInfo(1L)).thenReturn(mockUser);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getUserInfo_NotFound() throws Exception {
        when(userServices.getUserInfo(99L)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/users/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    void getUserInfoFromJwt_Success() throws Exception {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("1");
        UserInfoDTO mockUser = mock(UserInfoDTO.class);
        when(userServices.getUserInfo(1L)).thenReturn(mockUser);

        mockMvc.perform(get("/api/users/me")
                .principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    void getUserInfoFromJwt_Unauthorized_NullAuth() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Access denied, you must be authenticated"));
    }

    @Test
    void getUserInfoFromJwt_Unauthorized_NotAuthenticated() throws Exception {
        when(authentication.isAuthenticated()).thenReturn(false);

        mockMvc.perform(get("/api/users/me")
                .principal(authentication))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Access denied, you must be authenticated"));
    }

    @Test
    void getUserInfoFromJwt_NotFound() throws Exception {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("99");
        when(userServices.getUserInfo(99L)).thenThrow(new RuntimeException("Db Error"));

        mockMvc.perform(get("/api/users/me")
                .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }
}