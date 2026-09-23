package com.example.secureadaptive.dto;

public class OtpVerifyRequest {
    private Long userId;
    private String otp;
    private boolean trustDevice;
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public boolean isTrustDevice() { return trustDevice; }
    public void setTrustDevice(boolean trustDevice) { this.trustDevice = trustDevice; }
}
