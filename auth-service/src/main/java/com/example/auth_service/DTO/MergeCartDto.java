package com.example.auth_service.DTO;

public class MergeCartDto {
    private Long userId;
    private Long guestId;

    public MergeCartDto() {
    }

    public MergeCartDto(Long userId, Long guestId) {
        this.userId = userId;
        this.guestId = guestId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getGuestId() {
        return guestId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }
}
