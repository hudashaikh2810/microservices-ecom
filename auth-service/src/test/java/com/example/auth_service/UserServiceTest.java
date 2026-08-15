package com.example.auth_service;

import com.example.auth_service.DTO.UserDto;
import com.example.auth_service.Services.UserService;
import com.example.auth_service.entity.Role;
import com.example.auth_service.entity.User;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.initMocks(this);

    }
    @Test
    void save() {

        UserDto userDto = new UserDto();
        userDto.setUserName("ABC");
        userDto.setPassword("123");
        userDto.setRole("User");

        User user = new User();
        user.setUserName("ABC");
        user.setPassword("encodedPassword");

        Role r = new Role();
        r.setName("User");

        when(passwordEncoder.encode("123"))
                .thenReturn("encodedPassword");

        when(roleRepository.findByRolename("User"))
                .thenReturn(r);

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        User u = userService.saveUser(userDto);

        Assertions.assertNotNull(u);
    }
}
