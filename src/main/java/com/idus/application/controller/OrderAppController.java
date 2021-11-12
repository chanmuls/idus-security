package com.idus.application.controller;

import com.idus.application.response.OrderResponse;
import com.idus.application.response.UserResponse;
import com.idus.application.security.dto.PrincipalDetails;
import com.idus.application.service.OrderAppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ResponseHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping(value = "/orders")
@Api(value = "/orders", tags = "ORDER API")
public class OrderAppController {

    @Autowired
    private OrderAppService orderAppService;

    @ApiOperation(value = "주문 정보",
            produces = MediaType.APPLICATION_JSON_VALUE,
            responseHeaders = {
                    @ResponseHeader(name = HttpHeaders.CONTENT_TYPE, description = MediaType.APPLICATION_JSON_VALUE),
                    @ResponseHeader(name = HttpHeaders.AUTHORIZATION, description = "bearer token")
            })
    @GetMapping
    public List<OrderResponse> getAllByUser(@ApiIgnore @AuthenticationPrincipal PrincipalDetails principal) {
        return orderAppService.getAllByUser(principal);
    }
}
