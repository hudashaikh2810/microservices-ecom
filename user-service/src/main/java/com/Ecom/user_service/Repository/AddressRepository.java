package com.Ecom.user_service.Repository;
import com.Ecom.user_service.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    // Find all addresses for a specific user
    Optional<List<Address>> findByUserId(Long userId);
}

