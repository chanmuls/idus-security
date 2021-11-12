package com.idus.domain.order.repository;

import com.idus.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findAllByUserId(Long userId);

    List<Order> findAllByUserIdIn(List<Long> userIds);
}
