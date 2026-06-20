package com.example.auth_service.repository;

import com.example.auth_service.entity.RefreshToken;
import com.example.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByUser(User u);

    Optional<RefreshToken> findByToken(String token);
}
