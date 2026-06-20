package com.example.auth_service.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long tokenId;
    private String token;
    private Date expiryDate;
    @OneToOne
    @JoinColumn(name="userId")
    private User user;

    public RefreshToken() {
    }

    public RefreshToken(Long tokenId, String token, Date expiryDate, User user) {
        this.tokenId = tokenId;
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public String getToken() {
        return token;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public User getUser() {
        return user;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
