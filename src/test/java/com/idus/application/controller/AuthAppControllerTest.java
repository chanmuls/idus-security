package com.idus.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idus.application.request.SignInRequest;
import com.idus.application.request.SignUpRequest;
import com.idus.application.request.TokenRequest;
import com.idus.application.response.TokenResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthAppControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    void signUp_success() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest("백승호",
                "dev", "abcde가A1!", "01012345678", "user2@idus.com", "MALE");

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("nickname").exists())
                .andExpect(jsonPath("phoneNumber").exists())
                .andExpect(jsonPath("email").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    private MvcResult signIn() throws Exception {
        SignInRequest signInRequest = new SignInRequest("user@idus.com", "abcde가A1!");

        return mockMvc.perform(post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("grantType").exists())
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("accessTokenExpiresIn").exists())
                .andExpect(jsonPath("refreshToken").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    void signIn_success() throws Exception {
        signIn();
    }

    @Test
    void signIn_unauthorized() throws Exception {
        SignInRequest signInRequest = new SignInRequest("user@idus.com", "ww!");

        mockMvc.perform(post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_success() throws Exception {
        String content = signIn().getResponse().getContentAsString();
        TokenResponse tokenResponse = objectMapper.readValue(content, TokenResponse.class);

        TokenRequest tokenRequest = new TokenRequest(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());

        mockMvc.perform(post("/reissuance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("grantType").exists())
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("accessTokenExpiresIn").exists())
                .andExpect(jsonPath("refreshToken").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    void signout_success() throws Exception {
        String content = signIn().getResponse().getContentAsString();
        TokenResponse tokenResponse = objectMapper.readValue(content, TokenResponse.class);

        TokenRequest tokenRequest = new TokenRequest(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());

        mockMvc.perform(delete("/signout")
                        .header(HttpHeaders.AUTHORIZATION, "bearer " + tokenRequest.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }
}