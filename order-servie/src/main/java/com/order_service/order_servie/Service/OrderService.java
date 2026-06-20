package com.order_service.order_servie.Service;

import com.order_service.order_servie.DTO.*;
import com.order_service.order_servie.Entity.Order;
import com.order_service.order_servie.Entity.OrderItem;
import com.order_service.order_servie.Enums.OrderStatus;
import com.order_service.order_servie.Enums.PaymentStatus;
import com.order_service.order_servie.Enums.PaymentType;
import com.order_service.order_servie.Exceptions.*;
import com.order_service.order_servie.Mapper.OrderAddressMapper;
import com.order_service.order_servie.Repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {
    @Autowired
    private ProductServiceClient productServiceClient;

    @Autowired
    private InventoryServiceClient inventoryServiceClient;

    @Autowired
    private OrderAddressMapper orderAddressMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentServiceClient paymentServiceClient;


    public OrderDto createOrder(OrderRequest orderRequest) {
        Logger log = LoggerFactory.getLogger(this.getClass());

        log.info("Starting order creation for userId: {}", orderRequest.getUserId());

        // Lock the price
        List<String> skuIds = orderRequest.getStockReservationRequestList()
                .stream()
                .map(StockReservationRequest::getSku)
                .toList();

        log.debug("Fetching prices for {} SKUs: {}", skuIds.size(), skuIds);

        List<ProductPrice> productPrices = productServiceClient.callProductServiceFeignClient(skuIds);

        log.info("Successfully fetched {} product prices", productPrices.size());

        // Create a map for quick lookup of prices by skuId
        Map<String, ProductPrice> priceMap = productPrices.stream()
                .collect(Collectors.toMap(ProductPrice::getSkuId, Function.identity()));

        List<StockReservationRequest> stockReservationRequests = orderRequest.getStockReservationRequestList();
        String id = UUID.randomUUID().toString();
        StockRequest stockRequest = StockRequest.builder().stockReservationRequestList(stockReservationRequests).orderId(id).build();
        // Reserve inventory
        log.info("Attempting to reserve inventory for {} items", stockReservationRequests.size());
        BulkReservationResponse bulkReservationResponse = inventoryServiceClient.callInventoryFeignClient(stockRequest);

        if (!bulkReservationResponse.isSuccess()) {
            log.error("Inventory reservation failed for userId: {}. Response: {}",
                    orderRequest.getUserId(), bulkReservationResponse);
            throw new InventoryNotReservedException("Failed to reserve inventory for the order");
        }

        log.info("Inventory successfully reserved for userId: {}", orderRequest.getUserId());

        // Create order
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.CREATED);
        order.setDeliveryAddress(orderAddressMapper.convertToEntity(orderRequest.getOrderAddressDto()));
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setUserId(orderRequest.getUserId());
        order.setOrderTrackingId(id);

        log.debug("Order entity created with status: {}, paymentStatus: {}",
                OrderStatus.CREATED, PaymentStatus.PENDING);

        // Create order items
        log.info("Creating order items for {} SKUs", stockReservationRequests.size());

        List<OrderItem> orderItems = stockReservationRequests.stream()
                .map(reservationRequest -> {
                    String skuId = reservationRequest.getSku();
                    int quantity = reservationRequest.getQuantity();

                    log.info("Processing order item for SKU: {}, quantity: {}", skuId, quantity);

                    ProductPrice productPrice = priceMap.get(skuId);
                    if (productPrice == null) {
                        log.error("Price not found for SKU: {} while creating order for userId: {}",
                                skuId, orderRequest.getUserId());
                        throw new ProductPriceNotFound("Price not found for SKU: " + skuId);
                    }

                    Double pricePerUnit = productPrice.getPrice();
                    Double totalPrice = pricePerUnit * quantity;

                    log.debug("Order item calculated - SKU: {}, pricePerUnit: {}, totalPrice: {}",
                            skuId, pricePerUnit, totalPrice);

                    OrderItem orderItem = new OrderItem();
                    orderItem.setSkuId(skuId);
                    orderItem.setQuantity(quantity);
                    orderItem.setPricePerUnit(pricePerUnit);
                    orderItem.setTotalItemPrice(totalPrice);
                    orderItem.setOrder(order);

                    return orderItem;
                })
                .toList();

        order.setOrderItems(orderItems);

        double orderTotal = orderItems.stream()
                .mapToDouble(OrderItem::getTotalItemPrice)
                .sum();
        order.setAmountToPay(orderTotal);
        log.info("Order items created successfully. Total items: {}, Order total amount: {}",
                orderItems.size(), orderTotal);

       Order savedOrder;
       try{
           savedOrder= orderRepository.save(order);

       }
       catch(Exception e)
       {
           log.info("Order creating failed for order,Releasing the reserved stock");
          String message= inventoryServiceClient.callReleaseBulkReservationRequest(stockRequest);
           if(message==null || !message.contains("released successfully"))
           {
               throw new OrderCouldNotBeReleased("Order with tracking id="+stockRequest.getOrderId()+" could not be released");
           }
           throw new OrderCouldNotBeCreated("Order with tracking id="+stockRequest.getOrderId()+" could not be created");

       }
        // Save order (assuming you have orderRepository)
        // orderRepository.save(order);
        List<OrderItemDto> orderItemDto = savedOrder.getOrderItems().stream().map(OrderItem -> {
            return OrderItemDto.builder().orderId(OrderItem.getOrder().getId()).orderType(OrderItem.getOrderType())
                    .quantity(OrderItem.getQuantity()).pricePerUnit(OrderItem.getPricePerUnit())
                    .totalItemPrice(OrderItem.getTotalItemPrice())
                    .productId(OrderItem.getSkuId())
                    .build();

        }).toList();
        log.info("Order creation completed successfully for userId: {}, total amount: {}",
                orderRequest.getUserId(), orderTotal);
        return OrderDto.builder().id(savedOrder.getId()).orderTrackingId(savedOrder.getOrderTrackingId()).orderStatus(savedOrder.getOrderStatus())
                .createdAt(savedOrder.getCreatedAt()).amountToPay(savedOrder.getAmountToPay()).paymentType(savedOrder.getPaymentType())
                .paymentStatus(savedOrder.getPaymentStatus()).userId(savedOrder.getUserId()).orderItems(orderItemDto).build();
    }

    public void confirmBulkOrder(Long orderId) {
        //fetch order based on order-id
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderWithIdNotFoundException("Order with id= " + orderId + " could not be found"));
        List<StockReservationRequest> stockReservationRequest = order.getOrderItems().stream().map(orderItem -> {
            return StockReservationRequest.builder().sku(orderItem.getSkuId()).quantity(orderItem.getQuantity()).build();
        }).toList();
        StockRequest stockRequest = StockRequest.builder().stockReservationRequestList(stockReservationRequest).orderId(order.getOrderTrackingId()).build();
        String message = inventoryServiceClient.callConfirmBulkReservationRequest(stockRequest);
        if (message == null|| !message.contains("Order confrmed")) {
            throw new OrderCouldNotBeConfirmed("This order couldnot be confirmed");
        }
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

    }

    public boolean releaseBulkOrder(Long orderId) {
        //fetch order based on order-id
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderWithIdNotFoundException("Order with id= " + orderId + " could not be found"));
        List<StockReservationRequest> stockReservationRequest = order.getOrderItems().stream().map(orderItem -> {
            return StockReservationRequest.builder().sku(orderItem.getSkuId()).quantity(orderItem.getQuantity()).build();
        }).toList();
        StockRequest stockRequest = StockRequest.builder().stockReservationRequestList(stockReservationRequest).orderId(order.getOrderTrackingId()).build();
        String message = inventoryServiceClient.callReleaseBulkReservationRequest(stockRequest);
        if (message == null || !message.contains("released successfully")) {
            throw new OrderCouldNotBeReleased("This order could not be released");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.FAILED);
        orderRepository.save(order);
        return true;
    }

    public OrderDto getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderWithIdNotFoundException("Order with id= " + orderId + " could not be found"));
        List<OrderItemDto> orderItemDto = order.getOrderItems().stream().map(OrderItem -> {
            return OrderItemDto.builder().orderId(OrderItem.getOrder().getId()).orderType(OrderItem.getOrderType())
                    .quantity(OrderItem.getQuantity()).pricePerUnit(OrderItem.getPricePerUnit())
                    .totalItemPrice(OrderItem.getTotalItemPrice())
                    .productId(OrderItem.getSkuId())
                    .build();

        }).toList();
        return OrderDto.builder().orderItems(orderItemDto).orderStatus(order.getOrderStatus()).orderTrackingId(order.getOrderTrackingId())
                .amountToPay(order.getAmountToPay()).createdAt(order.getCreatedAt()).paymentStatus(order.getPaymentStatus())
                .paymentType(order.getPaymentType()).build();

    }

    //cancel order
    @Transactional
    public boolean cancelOrder(Long orderId) {
        boolean result = false;
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderWithIdNotFoundException("Order with id= " + orderId + " could not be found"));
        if (order.getOrderStatus().equals(OrderStatus.CANCELLED)) {
            throw new OrderAlreadyCancelled("Order with orderId =" + orderId + " is already cancelled");

        } else {
            if (order.getOrderStatus().equals(OrderStatus.CREATED)) {
                result = releaseBulkOrder(orderId);
            } else {
                if (order.getOrderStatus().equals((OrderStatus.CONFIRMED))) {
                    Map<String, Integer> orderItem =
                            order.getOrderItems()
                                    .stream()
                                    .collect(Collectors.toMap(
                                            OrderItem::getSkuId,
                                            OrderItem::getQuantity,
                                            Integer::sum   // 👈 merge function
                                    ));
                    result = inventoryServiceClient.callRestockInventoryOnCancel(order.getOrderTrackingId(), orderItem);
                    if(result)
                    {
                        order.setOrderStatus(OrderStatus.CANCELLED);
                    }
                }
            }

        }
        return result;
    }

    public InitiatePaymentResponse payNow(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderWithIdNotFoundException("Order with id= " + orderId + " could not be found"));
        InitiatePaymentRequest request=InitiatePaymentRequest.builder().orderId(orderId)
                .paymentType(PaymentType.ONLINE).amount(BigDecimal.valueOf(order.getAmountToPay()))
                .currency("inr").build();
        order.setPaymentStatus(PaymentStatus.INITIATED);
        orderRepository.save(order);
        return paymentServiceClient.initiatePaymentRequest(request);


    }

}
