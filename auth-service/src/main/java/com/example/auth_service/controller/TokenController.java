package com.example.auth_service.controller;

import com.example.auth_service.DTO.ResponseDto;
import com.example.auth_service.Exception.InvalidTokenException;
import com.example.auth_service.Exception.TokenExpired;
import com.example.auth_service.Services.RefreshTokenService;
import com.example.auth_service.Services.UserDetailServiceImplementation;
import com.example.auth_service.entity.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/token")
public class TokenController {
    private static final Logger log = LoggerFactory.getLogger(TokenController.class);
    @Autowired
    private UserDetailServiceImplementation  userDetailsService;
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/regenerate-access-token")
    public String reGenerateAccessToken(@RequestBody String refreshToken)
    {
        log.info("Checkig token for expiration");
        if (!jwtUtil.validateToken(refreshToken)) {
            log.info("Token is expired");
            throw new TokenExpired("Invalid or expired token");
        }
        log.info("Token not expired");

        String token = jwtUtil.extractToken(refreshToken);
        String userName = jwtUtil.extractUsername(refreshToken);
        String type = jwtUtil.extractType(refreshToken);

        boolean isValidType = "refreshtoken".equalsIgnoreCase(type);
        log.info("Going to check -db");
        boolean existsInDb = refreshTokenService.validateTokenInDb(token, userName);

        if (isValidType && existsInDb) {
            UserDetails user = userDetailsService.loadUserByUsername(userName);
            User u=userRepository.findByUserName(userName);
            List<String> roles=user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            return jwtUtil.generateToken(u.getId(),userName,roles);
        }

        throw new TokenExpired("Token expired or invalid");

    }

    @GetMapping("/regenerate-refreshToken")
    public ResponseEntity<?> regenerateRefreshToken(@RequestBody String refreshToken)
    {
        String token = jwtUtil.extractToken(refreshToken);
        String userName = jwtUtil.extractUsername(refreshToken);
        String type = jwtUtil.extractType(refreshToken);
log.info(userName);
        boolean isValidType = "refreshtoken".equalsIgnoreCase(type);
        boolean existsInDb = refreshTokenService.validateTokenInDb(token, userName);
        if(isValidType&&existsInDb)
        {
            refreshTokenService.deleteRefreshTokenIfExist(userName);
            UserDetails user = userDetailsService.loadUserByUsername(userName);
            List<String> roles=user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            User u=userRepository.findByUserName(userName);
            String acessToken=jwtUtil.generateToken(u.getId(),userName,roles);
            String refreshTokenNew=jwtUtil.createRefreshToken(userName);
            refreshTokenService.createRefreshToken(refreshTokenNew);
            var response=new ResponseDto();
            response.setAccessToken(acessToken);
            response.setRefreshToken(refreshTokenNew);
            return ResponseEntity.ok(response);


        }
        throw new InvalidTokenException("Invalid Token");

    }



}
