package com.idus.domain.user.repository;

import com.idus.domain.user.entity.RefreshToken;
import com.idus.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByToken(String refreshToken);

    Optional<RefreshToken> findByToken(String refreshToken);

    void deleteByUserId(Long userId);

    void deleteByUser(User user);
}
