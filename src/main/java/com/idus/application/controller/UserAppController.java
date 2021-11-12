package com.idus.application.controller;

import com.idus.application.request.SignInRequest;
import com.idus.application.response.TokenResponse;
import com.idus.application.response.UserResponse;
import com.idus.application.security.dto.PrincipalDetails;
import com.idus.application.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserAppController {

    @Autowired
    private UserAppService userAppService;

    @GetMapping
    public Page<UserResponse> getByPageable(@AuthenticationPrincipal PrincipalDetails principal,
                                            @RequestParam(required = false) String name,
                                            @RequestParam(required = false) String email,
                                            @PageableDefault(size=3, sort="id") Pageable pageable) {
        return userAppService.getByPageable(principal, name, email, pageable);
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal PrincipalDetails principal) {
        return userAppService.me(principal);
    }
}
