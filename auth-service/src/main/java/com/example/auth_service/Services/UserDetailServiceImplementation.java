package com.example.auth_service.Services;

import com.example.auth_service.entity.Role;
import com.example.auth_service.entity.User;
import com.example.auth_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@Service
public class UserDetailServiceImplementation implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.debug("Going to serch whether user with user exist or not");
        User u = userRepository.findByUserName(username);
        if (u != null) {
            log.info("User with user name exist");
            String[] roleNames = u.getRoles().stream()
                    .map(Role::getRoleName)
                    .toArray(String[]::new);
            log.debug("Return UserDetails object");
            return org.springframework.security.core.userdetails.User.withUsername(u.getUserName())
                    .password(u.getPassword())
                    .roles(roleNames)
                    .build();
        }
        return null;

    }
}
