package com.Ecom.user_service.Controller;

import com.Ecom.user_service.Dto.AddressDto;
import com.Ecom.user_service.Service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/address")
public class AddressController {
    private final Logger logger = LoggerFactory.getLogger(AddressController.class);
    @Autowired
    private AddressService addressService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createAddress(@RequestBody AddressDto addressDto, @PathVariable Long userId) {
        logger.info("Going to add a new address");
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.addAddress(userId, addressDto));
    }

    @GetMapping("/get/{addressId}")
    public ResponseEntity<?> getAddressWithId(@PathVariable Long addressId) {
        logger.info("Received request for retrieving address with id {}", addressId);
        return ResponseEntity.ok(addressService.getAddress(addressId));
    }

    @GetMapping("/get/all/address/{userId}")
    public ResponseEntity<?> getAllAddressWithUserId(@PathVariable Long userId) {
        logger.info("Received request for retrieving address with user id {}", userId);
        return ResponseEntity.ok(addressService.getAllUserAddress(userId));
    }

    @DeleteMapping("/delete/{addressId}")
    public ResponseEntity<?> deleteAddressWithId(@PathVariable Long addressId) {
        logger.info("Received request for deleting address with  id {}", addressId);
        addressService.delete(addressId);
        return ResponseEntity.ok("Address deleted successfully");
    }
    @PutMapping("/update/{addressId}")
    public ResponseEntity<?> updateAddress(@PathVariable Long addressId,@RequestBody AddressDto addressDto) {
        logger.info("Received request for updating address with  id {}",addressId);
        return ResponseEntity.ok(addressService.update(addressId,addressDto));
    }


}
