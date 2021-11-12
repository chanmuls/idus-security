package com.idus.application.controller;

import com.idus.application.response.TokenResponse;
import com.idus.application.security.provider.TokenProvider;
import com.idus.domain.order.entity.Order;
import com.idus.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserAppControllerTest {
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private OrderRepository orderRepository;

    private TokenResponse tokenResponse;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken("user@idus.com", "abcde가A1!");

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        tokenResponse = tokenProvider.generateToken(authentication);

        // 전체 실행은 테스트 개선 필요
        orderRepository.save(Order.builder()
                .payedAt(LocalDateTime.now())
                .productName("테스트")
                .userId(1L)
                .build());
    }

    @Test
    void me() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "bearer " + tokenResponse.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("nickname").exists())
                .andExpect(jsonPath("phoneNumber").exists())
                .andExpect(jsonPath("email").exists())
        ;
    }

    @Test
    void getByPageable() throws Exception {
        mockMvc.perform(get("/users")
                        .param("page", String.valueOf(0))
                        .header(HttpHeaders.AUTHORIZATION, "bearer " + tokenResponse.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].nickname").exists())
                .andExpect(jsonPath("$.content[0].phoneNumber").exists())
                .andExpect(jsonPath("$.content[0].email").exists())
        ;
    }

    @Test
    void getByPageable_search_name() throws Exception {
        mockMvc.perform(get("/users")
                        .param("name", "백")
                        .header(HttpHeaders.AUTHORIZATION, "bearer " + tokenResponse.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].nickname").exists())
                .andExpect(jsonPath("$.content[0].phoneNumber").exists())
                .andExpect(jsonPath("$.content[0].email").exists())
        ;
    }

    @Test
    void getByPageable_search_email() throws Exception {
        mockMvc.perform(get("/users")
                        .param("email", "user@")
                        .header(HttpHeaders.AUTHORIZATION, "bearer " + tokenResponse.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].nickname").exists())
                .andExpect(jsonPath("$.content[0].phoneNumber").exists())
                .andExpect(jsonPath("$.content[0].email").exists())
        ;
    }
}