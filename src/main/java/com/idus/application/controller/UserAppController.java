package com.idus.application.controller;

import com.idus.application.request.SignInRequest;
import com.idus.application.response.TokenResponse;
import com.idus.application.response.UserResponse;
import com.idus.application.security.dto.PrincipalDetails;
import com.idus.application.service.UserAppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ResponseHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
@Api(value = "/users", tags = "USER API")
public class UserAppController {

    @Autowired
    private UserAppService userAppService;

    @ApiOperation(value = "회원 리스트",
            produces = MediaType.APPLICATION_JSON_VALUE,
            responseHeaders = {
                    @ResponseHeader(name = HttpHeaders.CONTENT_TYPE, description = MediaType.APPLICATION_JSON_VALUE),
                    @ResponseHeader(name = HttpHeaders.AUTHORIZATION, description = "bearer token")
            })
    @GetMapping
    public Page<UserResponse> getByPageable(@ApiIgnore @AuthenticationPrincipal PrincipalDetails principal,
                                            @RequestParam(required = false) String name,
                                            @RequestParam(required = false) String email,
                                            @PageableDefault(size=3, sort="id") Pageable pageable) {
        return userAppService.getByPageable(principal, name, email, pageable);
    }

    @ApiOperation(value = "회원 정보",
            produces = MediaType.APPLICATION_JSON_VALUE,
            responseHeaders = {
                    @ResponseHeader(name = HttpHeaders.CONTENT_TYPE, description = MediaType.APPLICATION_JSON_VALUE),
                    @ResponseHeader(name = HttpHeaders.AUTHORIZATION, description = "bearer token")
            })
    @GetMapping("/me")
    public UserResponse me(@ApiIgnore @AuthenticationPrincipal PrincipalDetails principal) {
        return userAppService.me(principal);
    }
}
