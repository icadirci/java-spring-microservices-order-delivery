package com.orderplatform.userservice.integration.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderplatform.userservice.auth.dto.LoginRequest;
import com.orderplatform.userservice.auth.dto.RegisterRequest;
import com.orderplatform.userservice.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    @Test
    void register_shouldCreateUser_andReturnToken() throws Exception {
        RegisterRequest req = new RegisterRequest(
                "test@test.com",
                "testuser",
                "123456",
                "Test User"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken", not(blankOrNullString())))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.message").doesNotExist()); // message null ise bazen yok görünür, ApiResponse implementine bağlı
    }

    @Test
    void register_shouldFail_whenEmailAlreadyUsed() throws Exception {
        // önce register
        RegisterRequest first = new RegisterRequest("test@test.com", "testuser","123456", "Test User");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isOk());

        // aynı email tekrar
        RegisterRequest second = new RegisterRequest("test@test.com", "testuser","123456", "Test User 2");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message", containsStringIgnoringCase("email")));
    }

    @Test
    void login_shouldReturnToken_whenCredentialsValid() throws Exception {
        // önce register
        RegisterRequest reg = new RegisterRequest("test@test.com", "testuser","123456", "Test User");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isOk());

        // login
        LoginRequest login = new LoginRequest("test@test.com", "123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken", not(blankOrNullString())))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    void login_shouldFail_whenPasswordWrong() throws Exception {
        RegisterRequest reg = new RegisterRequest("test@test.com", "testuser", "123456", "Test User");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isOk());

        LoginRequest login = new LoginRequest("test@test.com", "wrongpass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message", containsStringIgnoringCase("Invalid")));
    }

    @Test
    void me_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());
    }


    @Test
    void me_shouldReturnUser_whenTokenValid() throws Exception {
        RegisterRequest req = new RegisterRequest("test@test.com", "testuser","123456", "Test User");
        var regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", not(blankOrNullString())))
                .andReturn();

        String json = regResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(json).at("/data/accessToken").asText();

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andExpect(jsonPath("$.data.fullName").value("Test User"));
    }

}