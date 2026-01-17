package com.orderplatform.orderservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderplatform.orderservice.order.dto.CreateOrderRequest;
import com.orderplatform.orderservice.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void cleanDb() {
        orderRepository.deleteAll();
    }

    @WithMockUser(
            username = "1",
            roles = {"USER"}
    )
    @Test
    void createOrder_shouldCreateOrderSuccessfully() throws Exception{
        CreateOrderRequest req = new CreateOrderRequest("Şanlıurfa türkiye");
        mockMvc.perform(post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", not(blankOrNullString())))
                .andReturn();
    }

    @Test
    void createOrderWithToken_shouldSuccessfully() throws Exception{
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3NjY3MDcyOTMsImV4cCI6MTc2NjcxMDg5M30.BBe2q97SYmltKZ4Y-xAf3srWkYSGpgpo8xFsla_qf1c";
        CreateOrderRequest req = new CreateOrderRequest("Şanlıurfa türkiye");

        mockMvc.perform(post("/api/orders/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", not(blankOrNullString())))
                .andReturn();
    }
}
