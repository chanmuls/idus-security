package com.idus.domain.order.service;


import com.idus.domain.order.entity.Order;
import com.idus.domain.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    public List<Order> getAllByUserIds(List<Long> userIds) {
        return orderRepository.findAllByUserIdIn(userIds);
    }
}
