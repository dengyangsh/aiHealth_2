package com.aihealth.first.repository;

import com.aihealth.first.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findByToken(String token);
    Optional<UserToken> findByUserId(Long userId);
    void deleteByUserId(Long userId);
} 