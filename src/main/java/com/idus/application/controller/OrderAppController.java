package com.idus.application.controller;

import com.idus.application.response.OrderResponse;
import com.idus.application.response.UserResponse;
import com.idus.application.security.dto.PrincipalDetails;
import com.idus.application.service.OrderAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/orders")
public class OrderAppController {

    @Autowired
    private OrderAppService orderAppService;


    @GetMapping
    public List<OrderResponse> getAllByUser(@AuthenticationPrincipal PrincipalDetails principal) {
        return orderAppService.getAllByUser(principal);
    }
}
