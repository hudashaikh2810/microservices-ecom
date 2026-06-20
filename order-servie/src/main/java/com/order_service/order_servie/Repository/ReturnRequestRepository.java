package com.order_service.order_servie.Repository;

import com.order_service.order_servie.Entity.OrderItem;
import com.order_service.order_servie.Entity.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest,Long> {
}
