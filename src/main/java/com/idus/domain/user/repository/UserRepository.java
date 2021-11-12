package com.idus.domain.user.repository;

import com.idus.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Page<User> findByNameContainingOrEmailContaining(String name, String email, Pageable pageable);

    Page<User> findByEmailContaining(String email, Pageable pageable);

    Page<User> findByNameContaining(String name, Pageable pageable);
}
