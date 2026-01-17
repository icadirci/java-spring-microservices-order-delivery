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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {
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
    void shouldAllowAccess_whenUserHasAdminRole() throws Exception{
        RegisterRequest req = new RegisterRequest("test@test.com", "testuser","123456", "Test User");
        var regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", not(blankOrNullString())))
                .andReturn();

        String json = regResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(json).at("/data/accessToken").asText();

        mockMvc.perform(get("/api/users/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn403_whenUserIsNotAdmin() throws Exception{
        RegisterRequest req = new RegisterRequest("test@test.com", "testuser","123456", "Test User");
        var regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", not(blankOrNullString())))
                .andReturn();

        String json = regResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(json).at("/data/accessToken").asText();

        mockMvc.perform(get("/api/users/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

}
