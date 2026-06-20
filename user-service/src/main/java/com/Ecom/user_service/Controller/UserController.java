package com.Ecom.user_service.Controller;

import com.Ecom.user_service.Dto.UserDto;
import com.Ecom.user_service.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/user/addUser")
    public ResponseEntity<?> addUser(@RequestBody UserDto userDto)
    {
        logger.info("Adding user with username=",userDto.getEmail());
        logger.info("Going to call userService method");
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(userDto));
    }

    @GetMapping("/all/user/{authServiceId}")
    public ResponseEntity<?> getUserWithAuthServiceId(@PathVariable Long authServiceId)
    {
        logger.info("Retriving user with id{}",authServiceId);
        return ResponseEntity.ok(userService.getUserByAuthServiceId(authServiceId));
    }
    @GetMapping("/all/userEmail/{email}")
    public ResponseEntity<?> getUserWithEmail(@PathVariable String email)
    {
        logger.info("Retriving user with email{}",email);
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUserWthUserId(@PathVariable Long userId)
    {
        logger.info("Request received for deleting user with id",userId);
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/updates/{authServiceId}")
    public ResponseEntity<?> updateUserByAuthServiceId(@PathVariable Long authServiceId,@RequestBody UserDto userDto)
    {
        logger.info("Request received to update user with authServiceId");
        return ResponseEntity.ok(userService.updateUserByAuthUserId(authServiceId,userDto));

    }


}
