package com.example.secureadaptive.dto;

public class AuthResponse {
    private String token;
    private boolean requiresOtp;
    private Long userId;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String token, boolean requiresOtp, Long userId, String message) {
        this.token = token;
        this.requiresOtp = requiresOtp;
        this.userId = userId;
        this.message = message;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public boolean isRequiresOtp() { return requiresOtp; }
    public void setRequiresOtp(boolean requiresOtp) { this.requiresOtp = requiresOtp; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
