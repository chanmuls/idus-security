package com.idus.application.controller;

import com.idus.application.request.SignInRequest;
import com.idus.application.request.SignUpRequest;
import com.idus.application.request.TokenRequest;
import com.idus.application.response.UserResponse;
import com.idus.application.security.dto.PrincipalDetails;
import com.idus.application.service.UserAppService;
import com.idus.application.response.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthAppController {
    @Autowired
    private UserAppService userAppService;

    @PostMapping("/signin")
    public TokenResponse signIn(@Valid @RequestBody SignInRequest signInRequest) {
        return userAppService.signIn(signInRequest);
    }

    @PostMapping("/reissuance")
    public TokenResponse reissuance(@Valid @RequestBody TokenRequest tokenRequest) {
        return userAppService.reissuance(tokenRequest);
    }

    @PostMapping("/signup")
    public UserResponse signUp(@RequestBody SignUpRequest signUpRequest) {
        return userAppService.signUp(signUpRequest);
    }

    @DeleteMapping("/signout")
    public void signOut(@AuthenticationPrincipal PrincipalDetails principal) {
        userAppService.signOut(principal);
    }
}
