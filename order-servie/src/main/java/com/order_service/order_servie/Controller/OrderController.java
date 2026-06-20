package com.order_service.order_servie.Controller;

import com.order_service.order_servie.DTO.InitiatePaymentResponse;
import com.order_service.order_servie.DTO.OrderDto;
import com.order_service.order_servie.DTO.OrderRequest;
import com.order_service.order_servie.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        OrderDto orderDto = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(orderDto);
    }

    @PostMapping("/confirm/{id}")
    public ResponseEntity<?> confirmOrder(@PathVariable Long id) {
        orderService.confirmBulkOrder(id);
        return ResponseEntity.ok("Order confirmed");
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        boolean result = orderService.cancelOrder(id);
        if (result) {
            return ResponseEntity.ok("Order cancelled");
        } else {
            return ResponseEntity.badRequest().body("Order cannot be cancelled");
        }

    }

    @PostMapping("/payNow/{id}")
    public ResponseEntity<?> payNow(@PathVariable Long orderId) {
        InitiatePaymentResponse response=orderService.payNow(orderId);
        if(response!=null)
        {
            return ResponseEntity.ok(response);

        }
        else{
          return  ResponseEntity.internalServerError().body("Something wrong with payment service");
        }
    }

    @GetMapping("/orderDetails/{id}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long id) {
        OrderDto dto = orderService.getOrderDetails(id);
        return ResponseEntity.ok(dto);
    }


}
