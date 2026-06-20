package com.order_service.order_servie.Service;

import com.order_service.order_servie.DTO.ReturnRequest1;
import com.order_service.order_servie.Entity.Order;
import com.order_service.order_servie.Entity.OrderItem;
import com.order_service.order_servie.Entity.ReturnRequest;
import com.order_service.order_servie.Enums.ReturnItemStatus;
import com.order_service.order_servie.Enums.ReturnStatus;
import com.order_service.order_servie.Exceptions.OrderWithIdNotFoundException;
import com.order_service.order_servie.Repository.OrderRepository;
import com.order_service.order_servie.Repository.ReturnRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class ReturnService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    private ReturnRequestRepository returnRequestRepository;


    //create return order
    //orderId,List<OrderItemId>
    //reason
    //status=delivered then only return
    //else cannot return
    //create return request,order status changed to partial returned
    public void createReturnRequest(ReturnRequest1 returnRequest) {
        Order order = orderRepository.findById(returnRequest.getOrderId()).orElseThrow(() -> new OrderWithIdNotFoundException("Order with id=" + returnRequest.getOrderId() + " could not be found"));
        OrderItem returnOrderItem = order.getOrderItems()
                .stream()
                .filter(orderItem ->
                        Objects.equals(orderItem.getId(), returnRequest.getOrderItemId())
                )
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order item not found"));
        String uuid = UUID.randomUUID().toString() + order.getOrderTrackingId() + LocalDateTime.now();
        ReturnRequest returnRequestSaved = ReturnRequest.builder().requestedAt(LocalDateTime.now()).returnStatus(ReturnStatus.REQUESTED)
                .reason(returnRequest.getReturnReason()).quantityReturned(returnRequest.getQuantityReturned())
                .refundAmount(returnRequest.getQuantityReturned() * returnOrderItem.getPricePerUnit())
                .orderItem(returnOrderItem).originalOrder(order).returnTrackingId(uuid)
                .returnItemStatus(ReturnItemStatus.INITIATED).build();
        returnRequestRepository.save(returnRequestSaved);
    }




}
