package com.order_service.order_servie.Repository;

import com.order_service.order_servie.Entity.OrderInfo;
import com.order_service.order_servie.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderInfoRepository extends JpaRepository<OrderInfo,Long> {
}
