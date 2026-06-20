package com.example.auth_service;

import com.example.auth_service.Services.UserDetailServiceImplementation;
import com.example.auth_service.entity.Role;
import com.example.auth_service.entity.User;
import com.example.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class UserDetailServiceImplementationTest {
    @InjectMocks
    UserDetailServiceImplementation userDetailServiceImplementation;
    @Mock
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void loadUserByUsernameTest() {
        User u = new User();
        u.setUserName("ABC");
        u.setPassword("123");
        Role r = new Role();
        r.setName("User");
        u.getRoles().add(r);
        when(userRepository.findByUserName("ABC")).thenReturn(u);
        UserDetails userDetails = userDetailServiceImplementation.loadUserByUsername("ABC");
        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals("ABC",userDetails.getUsername());
    }


}
