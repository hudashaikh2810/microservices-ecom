package com.order_service.order_servie.Repository;

import com.order_service.order_servie.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
