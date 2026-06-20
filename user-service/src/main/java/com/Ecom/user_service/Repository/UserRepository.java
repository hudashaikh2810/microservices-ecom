package com.Ecom.user_service.Repository;

import com.Ecom.user_service.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by auth-service userId
    Optional<User> findByAuthUserId(Long authUserId);

    // Find user by email
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

}

