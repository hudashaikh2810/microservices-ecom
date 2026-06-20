package com.example.auth_service.Services;

import com.example.auth_service.DTO.UserCreatedEvent;
import com.example.auth_service.DTO.UserDto;
import com.example.auth_service.entity.Role;
import com.example.auth_service.entity.User;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.generators.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private RoleRepository roleRepository;

    public User saveUser(UserDto userDto) {
        User u = new User();
        u.setUserName(userDto.getUserName());
        u.setPassword(passwordEncoder.encode(userDto.getPassword()));
        if (userDto.getRole() != null) {
            Role role = roleRepository.findByRolename(userDto.getRole());
            u.getRoles().add(role);
        }

        try {
            log.info("User is going to be saved");
           User createdUser= userRepository.save(u);
            log.info("User  saved successfully");
            return createdUser;

        } catch (DataAccessException dae) {
            log.error("Error in saving user " + dae.getMessage());
        }
        return null;

    }

}
