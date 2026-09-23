package com.example.secureadaptive.controller;

import com.example.secureadaptive.entity.User;
import com.example.secureadaptive.repository.*;
import com.example.secureadaptive.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
public class SecurityController {

    @Autowired private UserRepository userRepository;
    @Autowired private SessionRepository sessionRepository;
    @Autowired private TrustedDeviceRepository trustedDeviceRepository;
    @Autowired private LoginHistoryRepository loginHistoryRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();

        Map<String, Object> data = new HashMap<>();
        data.put("activeSessions", sessionRepository.findByUserIdAndRevokedFalse(user.getId()));
        data.put("trustedDevices", trustedDeviceRepository.findByUserIdAndRevokedFalse(user.getId()));
        data.put("loginHistory", loginHistoryRepository.findByUserIdOrderByLoginTimestampDesc(user.getId()));

        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/trusted-device/{id}")
    public ResponseEntity<?> revokeDevice(@PathVariable Long id, Authentication auth) {
        TrustedDevice device = trustedDeviceRepository.findById(id).orElseThrow();
        device.setRevoked(true);
        trustedDeviceRepository.save(device);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/session/{id}")
    public ResponseEntity<?> revokeSession(@PathVariable Long id, Authentication auth) {
        Session session = sessionRepository.findById(id).orElseThrow();
        session.setRevoked(true);
        sessionRepository.save(session);
        return ResponseEntity.ok().build();
    }
}
