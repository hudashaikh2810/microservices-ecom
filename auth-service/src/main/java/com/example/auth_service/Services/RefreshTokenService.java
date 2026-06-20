package com.example.auth_service.Services;

import com.example.auth_service.Exception.InvalidTokenException;
import com.example.auth_service.entity.RefreshToken;
import com.example.auth_service.entity.User;
import com.example.auth_service.repository.JwtRepository;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class RefreshTokenService {
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtRepository jwtRepository;
    @Autowired
    JwtUtil jwtUtil;

    public void createRefreshToken(String refreshToken) {
        String userName = jwtUtil.extractUsername(refreshToken);
        if (userName != null) {
            RefreshToken refToken = new RefreshToken();
            refToken.setUser(userRepository.findByUserName(userName));
            String token = jwtUtil.extractToken(refreshToken);
            refToken.setToken(token);
            refToken.setExpiryDate(jwtUtil.extractExpiration(refreshToken));
            jwtRepository.save(refToken);
            log.info("Refresh token saved succcessfully");
        }


    }


    public void deleteRefreshTokenIfExist(String userName)
    {
        log.info("Going to delete the token");
        User u=userRepository.findByUserName(userName);
        jwtRepository.findByUser(u).ifPresent(jwtRepository::delete);
        log.info("User deleted");

    }
    public boolean validateTokenInDb(String refreshToken,String userName)
    {
        return Optional.ofNullable(userRepository.findByUserName(userName))
                .flatMap(jwtRepository::findByUser)
                .filter(token -> token.getToken().equals(refreshToken))
                .map(token -> true)
                .orElseThrow(() -> new InvalidTokenException("Token is invalid"));
    }

}
