package com.idus.domain.order.repository;

import com.idus.domain.order.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void save_success() {
        orderRepository.save(Order.builder()
                .payedAt(LocalDateTime.now())
                .productName("테스트")
                .userId(1L)
                .build());
    }
}