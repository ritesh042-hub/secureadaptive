package com.example.secureadaptive.service;

import com.example.secureadaptive.dto.*;
import com.example.secureadaptive.entity.*;
import com.example.secureadaptive.repository.*;
import com.example.secureadaptive.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Random;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private LoginHistoryRepository loginHistoryRepository;
    @Autowired private OtpVerificationRepository otpVerificationRepository;
    @Autowired private TrustedDeviceRepository trustedDeviceRepository;
    @Autowired private SessionRepository sessionRepository;
    
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    
    @Autowired private org.springframework.mail.javamail.JavaMailSender mailSender;
    
    @org.springframework.beans.factory.annotation.Value("${mail.from:example@gmail.com}")
    private String mailFrom;

    @Autowired private GeoLocationService geoLocationService;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);
        return new AuthResponse(null, false, user.getId(), "Registration successful");
    }

    public AuthResponse login(LoginRequest req, HttpServletRequest request) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            recordLoginAttempt(user, request, "FAILED_PASSWORD", false);
            throw new RuntimeException("Invalid credentials");
        }

        boolean requireOtp = checkAdaptiveRules(user, request);

        if (requireOtp) {
            recordLoginAttempt(user, request, "PENDING_OTP", true);
            generateAndSendOtp(user);
            return new AuthResponse(null, true, user.getId(), "OTP sent to your registered email");
        } else {
            recordLoginAttempt(user, request, "SUCCESS", false);
            String token = jwtUtil.generateToken(user.getEmail());
            createSession(user, token);
            return new AuthResponse(token, false, user.getId(), "Login successful");
        }
    }

    public AuthResponse verifyOtp(OtpVerifyRequest req, HttpServletRequest request) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        OtpVerification otpVerification = otpVerificationRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new RuntimeException("No OTP found"));

        if (otpVerification.isVerified() || otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired or already verified");
        }

        if (otpVerification.getAttemptCount() >= 3) {
            throw new RuntimeException("Max OTP attempts exceeded");
        }

        otpVerification.setAttemptCount(otpVerification.getAttemptCount() + 1);
        
        // For MVP, if it matches plain text or hashed. Since we generated plain text in console, we compare hash.
        if (!passwordEncoder.matches(req.getOtp(), otpVerification.getOtpHash())) {
            otpVerificationRepository.save(otpVerification);
            throw new RuntimeException("Invalid OTP");
        }

        otpVerification.setVerified(true);
        otpVerificationRepository.save(otpVerification);

        if (req.isTrustDevice()) {
            createTrustedDevice(user, request);
        }

        List<LoginHistory> histories = loginHistoryRepository.findTop1ByUserIdOrderByLoginTimestampDesc(user.getId());
        if (!histories.isEmpty()) {
            LoginHistory h = histories.get(0);
            h.setOtpVerified(true);
            h.setLoginStatus("SUCCESS");
            loginHistoryRepository.save(h);
        }

        String token = jwtUtil.generateToken(user.getEmail());
        createSession(user, token);
        return new AuthResponse(token, false, user.getId(), "OTP Verified");
    }

    private boolean checkAdaptiveRules(User user, HttpServletRequest request) {
        String currentIp = getClientIp(request);
        String currentUserAgent = request.getHeader("User-Agent");
        
        // MVP logic: If there's a trusted device that matches user-agent/IP, skip OTP
        List<TrustedDevice> devices = trustedDeviceRepository.findByUserIdAndRevokedFalse(user.getId());
        for (TrustedDevice d : devices) {
            if (d.getExpiresAt().isAfter(LocalDateTime.now()) && 
                currentUserAgent != null && currentUserAgent.contains(d.getBrowser())) {
                d.setLastUsedAt(LocalDateTime.now());
                trustedDeviceRepository.save(d);
                return false;
            }
        }
        
        // Check if new IP or first login
        List<LoginHistory> histories = loginHistoryRepository.findByUserIdOrderByLoginTimestampDesc(user.getId());
        if (histories.isEmpty()) return true; // first login
        
        LoginHistory lastSuccess = histories.stream().filter(h -> "SUCCESS".equals(h.getLoginStatus())).findFirst().orElse(null);
        if (lastSuccess == null) return true;
        
        if (!currentIp.equals(lastSuccess.getIpAddress())) return true;
        
        return false;
    }

    private void generateAndSendOtp(User user) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        try {
            org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(user.getEmail());
            message.setSubject("SecureAdaptive Login Verification Code");
            message.setText("Hello,\n\nYour SecureAdaptive verification code is:\n\n" + otp + "\n\nThis code is valid for the configured OTP expiration period.\n\nIf you did not attempt to log in, please secure your account.\n\nRegards,\nSecureAdaptive Security Team");
            
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send verification email. Please try again.");
        }
        
        OtpVerification verification = new OtpVerification();
        verification.setUserId(user.getId());
        verification.setOtpHash(passwordEncoder.encode(otp));
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpVerificationRepository.save(verification);
    }

    private void recordLoginAttempt(User user, HttpServletRequest req, String status, boolean otpReq) {
        LoginHistory h = new LoginHistory();
        h.setUserId(user.getId());
        String ip = getClientIp(req);
        h.setIpAddress(ip);
        
        // Fetch GeoLocation asynchronously or inline (inline for MVP)
        GeoLocationService.GeoLocation geo = geoLocationService.getGeoLocation(ip);
        h.setIpAddress(geo.getIp()); // Update IP if local was resolved to public
        h.setCity(geo.getCity());
        h.setState(geo.getRegion());
        h.setCountry(geo.getCountry());
        h.setLatitude(geo.getLatitude());
        h.setLongitude(geo.getLongitude());
        
        h.setBrowser(parseBrowser(req.getHeader("User-Agent")));
        h.setOperatingSystem(parseOs(req.getHeader("User-Agent")));
        h.setLoginStatus(status);
        h.setOtpRequired(otpReq);
        loginHistoryRepository.save(h);
    }

    private void createTrustedDevice(User user, HttpServletRequest req) {
        TrustedDevice t = new TrustedDevice();
        t.setUserId(user.getId());
        t.setBrowser(parseBrowser(req.getHeader("User-Agent")));
        t.setOperatingSystem(parseOs(req.getHeader("User-Agent")));
        t.setIpAddress(getClientIp(req));
        t.setDeviceTokenHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        t.setExpiresAt(LocalDateTime.now().plusDays(30));
        t.setLastUsedAt(LocalDateTime.now());
        trustedDeviceRepository.save(t);
    }

    private void createSession(User user, String token) {
        Session s = new Session();
        s.setUserId(user.getId());
        s.setSessionTokenHash(token); // Store raw token or use SHA-256, BCrypt fails > 72 bytes
        s.setExpiresAt(LocalDateTime.now().plusHours(24));
        sessionRepository.save(s);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
    
    private String parseBrowser(String ua) {
        if (ua == null) return "Unknown";
        if (ua.contains("Chrome")) return "Chrome";
        if (ua.contains("Firefox")) return "Firefox";
        if (ua.contains("Safari")) return "Safari";
        return "Other";
    }
    
    private String parseOs(String ua) {
        if (ua == null) return "Unknown";
        if (ua.contains("Windows")) return "Windows";
        if (ua.contains("Mac")) return "macOS";
        if (ua.contains("Linux")) return "Linux";
        if (ua.contains("Android")) return "Android";
        if (ua.contains("iPhone")) return "iOS";
        return "Other";
    }
}
