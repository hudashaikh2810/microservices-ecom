package com.Ecom.cart_service.cart_service.Service;

import com.Ecom.cart_service.cart_service.DTO.*;
import com.Ecom.cart_service.cart_service.Entity.Cart;
import com.Ecom.cart_service.cart_service.Entity.CartItem;
import com.Ecom.cart_service.cart_service.Exception.CartNotFoundException;
import com.Ecom.cart_service.cart_service.FeignClient.ProductServiceClient;
import com.Ecom.cart_service.cart_service.HelperPackage.MappperClass;
import com.Ecom.cart_service.cart_service.Repository.CartItemRepository;
import com.Ecom.cart_service.cart_service.Repository.CartRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private RedisService redisService;
    @Autowired
    private FetchCartObject fetchCartObject;
    @Autowired
    private CartMergeService mergeStrategy;
    @Autowired
    private MappperClass mapperClass;
    @Autowired
    private ProductClientService productClientService;


    public CartDto createCart(CartDto cartDto) {
        log.info("Converting CartDto to dto for user={} & saving", cartDto.getUserId());
        Cart savedCart = mapperClass.convertToEntity(cartDto);
        log.info("Now going to save cart with info {} {} {} {}", cartDto.getUserId(), cartDto.getLastUpdated(), cartDto.getItems());
        log.info("Cart id before saving is {}", savedCart.getId());
        Cart c = cartRepository.save(savedCart);
        log.info("Converted to dto successfully nd saved to db");

        return mapperClass.convertToDto(c);
    }

    public void handleUserCartCreation(UserCreatedEvent event) {

        // 1. Idempotency check
        Cart existingCart = cartRepository.findByUserId(event.getUserId()).orElse(null);

        if (existingCart != null) {
            log.info("🟢 Idempotent: Cart already exists for userId={}, skipping creation", event.getUserId());
            return;
        }

        // 2. Create new cart
        log.info("🟡 No cart found. Creating new cart for userId={}", event.getUserId());

        CartDto newCart = CartDto.builder()
                .lastUpdated(LocalDateTime.now())
                .items(new ArrayList<>())
                .userId(event.getUserId())
                .build();

        CartDto savedCart = createCart(newCart);
        Cart savedEntity = mapperClass.convertToEntity(savedCart);

        // 3. Merge guest cart (if exists)
        if (event.getGuestId() != null) {
            cartRepository.findByGuestId(event.getGuestId()).ifPresent(guestCart -> {
                log.info("Merging guest cart {} into user cart {}", event.getGuestId(), event.getUserId());
                mergeStrategy.mergeGuestUserCart(savedEntity, guestCart);
            });
        }
    }


    public CartDto addItem(Long guestId, Long userId, CartItemDto itemDto) throws ServiceUnavailableException {
        Cart cart = null;
        cart = fetchCartObject.fetchCartObject(userId, guestId);
        Double price = productClientService.getPrice(itemDto.getSkuId(), cart);


        log.info("Adding item to cart | userId={} | productId={},guestId={}", userId, itemDto.getSkuId(), guestId);
        // find if product already present in cart
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(ci -> ci.getSkuId().equals(itemDto.getSkuId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // product already present → increase quantity
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + 1);
            if (price != null) {
                existingItem.setPricePerUnit(price);

            }
            log.info("Item already exists in cart. Increasing quantity. New quantity={}", existingItem.getQuantity());
        } else {
            if(price==null)
            {
                throw new ServiceUnavailableException("Product service is temporarily unavailable. Please try again in a few moments.");

            }
            // new item → add it
            CartItem newItem = mapperClass.convertToEntity(itemDto, cart);
            newItem.setPricePerUnit(price);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
            log.info("Item added to cart");
        }



        cart.setLastUpdated(LocalDateTime.now());

        Cart saved = cartRepository.save(cart);
        CartDto savedDto = mapperClass.convertToDto(saved);

        log.info("Going to update the cache for it");
        updateCache(guestId, userId, savedDto);
        return savedDto;
    }

    private void updateCache(Long guestId, Long userId, CartDto savedDto) {
        if (guestId != null && userId == null) {
            log.info("Going to wrtie into guest cart in redis");
            redisService.writeToCacheGuestId(guestId, savedDto);
        } else {
            log.info("Going to write into user cart in redis");
            redisService.writeToCache(userId, savedDto);
        }
    }

    public CartDto removeItem(Long guestId, Long userId, CartItemDto itemDto) {

        Cart cart = fetchCartObject.fetchCartObject(userId, guestId);
        Double price = productClientService.getPrice(itemDto.getSkuId(), cart);

        log.info("🛒 removeItem request | userId={} | productId={} | qtyToRemove={}", userId, itemDto.getSkuId(), itemDto.getQuantity());

        List<CartItem> items = cart.getItems();

        Optional<CartItem> existingItemOpt = items.stream()
                .filter(i -> i.getSkuId().equals(itemDto.getSkuId()))
                .findFirst();

        if (existingItemOpt.isEmpty()) {
            log.warn("❌ Product {} not found in cart for user {}", itemDto.getSkuId(), userId);
            throw new RuntimeException("Product not found in cart");
        }

        CartItem existingItem = existingItemOpt.get();


        if (existingItem.getQuantity() == 1) {
            log.info("🗑 Removing product {} completely from cart", itemDto.getSkuId());
            items.remove(existingItem);
        } else {
            existingItem.setQuantity(existingItem.getQuantity() - 1);
            log.info("➖ Reduced quantity of product {} to {}", itemDto.getSkuId(), existingItem.getQuantity());
        }

        existingItem.setPricePerUnit(price);


        cart.setLastUpdated(LocalDateTime.now());

        Cart saved = cartRepository.save(cart);
        CartDto savedDto = mapperClass.convertToDto(saved);
        log.info("✅ removeItem success | newCartTotal={} | userId={}", userId);
        log.info("Going to update the cache for it");
        updateCache(guestId, userId, savedDto);
        return savedDto;
    }

    public CartDetailDto getCartByUser(Long userId, Long guestId) {
        log.info("Going to get cart");
        Cart cart = fetchCartObject.fetchCartObject(userId, guestId);

        log.info("Got cart for user cart-id {}", cart.getId());
        List<CartItemDetailDto> listItems = cart.getItems().stream()
                .map(this::processCartItem)
                .collect(Collectors.toList());

        Double cartTotal = listItems.stream()
                .map(CartItemDetailDto::getTotalPrice)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        return CartDetailDto.builder()
                .userId(cart.getUserId())
                .guestId(cart.getGuestId())
                .lastUpdated(LocalDateTime.now())
                .items(listItems)
                .cartTotal(cartTotal)
                .build();
    }

    private CartItemDetailDto processCartItem(CartItem cartItem) {
        log.info("Going to fetch product details with skuId {}", cartItem.getSkuId());
        CartItemDetailDto dto = productClientService.fetchProductDetail(cartItem.getSkuId());
        dto.setId(cartItem.getId());
        // Sync price: use product service price if available, otherwise use cached price
        Double finalPrice = dto.getPricePerUnit() != null
                ? dto.getPricePerUnit()
                : cartItem.getPricePerUnit();

        // Update cart item price if it changed from product service
        if (dto.getPricePerUnit() != null &&
                !dto.getPricePerUnit().equals(cartItem.getPricePerUnit())) {
            cartItem.setPricePerUnit(dto.getPricePerUnit());
            cartItemRepository.save(cartItem);
        }

        dto.setPricePerUnit(finalPrice);
        dto.setQuantity(cartItem.getQuantity());
        dto.setTotalPrice(finalPrice * cartItem.getQuantity());

        return dto;
    }


}
