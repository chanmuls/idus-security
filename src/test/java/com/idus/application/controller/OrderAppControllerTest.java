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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderAppControllerTest {
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

        orderRepository.save(Order.builder()
                .payedAt(LocalDateTime.now())
                .productName("테스트")
                .userId(1L)
                .build());
    }

    @Test
    void getAllByUser() throws Exception {
        mockMvc.perform(get("/orders")
                        .header(HttpHeaders.AUTHORIZATION, "bearer " + tokenResponse.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].productName").exists())
                .andExpect(jsonPath("$[0].payedAt").exists())
        ;
    }

}