package com.idus.domain.order.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.idus.domain.user.type.UserGender;
import com.idus.domain.user.type.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GenericGenerator(name = "orderIdGenerator", strategy = "com.idus.domain.order.generator.OrderIdGenerator")
    @GeneratedValue(generator = "orderIdGenerator")
    private String id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "payed_at")
    private LocalDateTime payedAt;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
