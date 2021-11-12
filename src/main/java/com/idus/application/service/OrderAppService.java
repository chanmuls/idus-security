package com.idus.application.service;

import com.idus.application.response.OrderResponse;
import com.idus.application.security.dto.PrincipalDetails;
import com.idus.domain.order.entity.Order;
import com.idus.domain.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderAppService {
    @Autowired
    private OrderService orderService;

    public List<OrderResponse> getAllByUser(PrincipalDetails principal) {
        List<Order> orders = orderService.getAllByUserId(principal.getUser().getId());

        return orders.stream().map(order -> OrderResponse.builder()
                        .id(order.getId())
                        .payedAt(order.getPayedAt())
                        .productName(order.getProductName())
                        .build())
                .collect(Collectors.toList());
    }
}
