package com.idus.application.controller;

import com.idus.application.request.SignInRequest;
import com.idus.application.request.SignUpRequest;
import com.idus.application.request.TokenRequest;
import com.idus.application.response.UserResponse;
import com.idus.application.security.dto.PrincipalDetails;
import com.idus.application.service.UserAppService;
import com.idus.application.response.TokenResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ResponseHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@Api(value = "/", tags = "AUTH API")
public class AuthAppController {
    @Autowired
    private UserAppService userAppService;

    @ApiOperation(value = "로그인",
            produces = MediaType.APPLICATION_JSON_VALUE,
            responseHeaders = {
                    @ResponseHeader(name = HttpHeaders.CONTENT_TYPE, description = MediaType.APPLICATION_JSON_VALUE)
            })
    @PostMapping("/signin")
    public TokenResponse signIn(@Valid @RequestBody SignInRequest signInRequest) {
        return userAppService.signIn(signInRequest);
    }

    @ApiOperation(value = "토큰 재발행",
            produces = MediaType.APPLICATION_JSON_VALUE,
            responseHeaders = {
                    @ResponseHeader(name = HttpHeaders.CONTENT_TYPE, description = MediaType.APPLICATION_JSON_VALUE)
            })
    @PostMapping("/reissuance")
    public TokenResponse reissuance(@Valid @RequestBody TokenRequest tokenRequest) {
        return userAppService.reissuance(tokenRequest);
    }

    @ApiOperation(value = "회원 가입",
            produces = MediaType.APPLICATION_JSON_VALUE,
            responseHeaders = {
                    @ResponseHeader(name = HttpHeaders.CONTENT_TYPE, description = MediaType.APPLICATION_JSON_VALUE)
            })
    @PostMapping("/signup")
    public UserResponse signUp(@RequestBody SignUpRequest signUpRequest) {
        return userAppService.signUp(signUpRequest);
    }

    @ApiOperation(value = "로그아웃",
            produces = MediaType.APPLICATION_JSON_VALUE,
            responseHeaders = {
                    @ResponseHeader(name = HttpHeaders.CONTENT_TYPE, description = MediaType.APPLICATION_JSON_VALUE),
                    @ResponseHeader(name = HttpHeaders.AUTHORIZATION, description = "bearer token")
            })
    @DeleteMapping("/signout")
    public void signOut(@ApiIgnore @AuthenticationPrincipal PrincipalDetails principal) {
        userAppService.signOut(principal);
    }
}
