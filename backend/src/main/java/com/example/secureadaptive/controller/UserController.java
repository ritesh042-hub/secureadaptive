package com.example.secureadaptive.controller;

import com.example.secureadaptive.entity.User;
import com.example.secureadaptive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        return userRepository.findByEmail(auth.getName())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/theme")
    public ResponseEntity<?> updateTheme(@RequestBody ThemeRequest req, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        user.setThemePreference(req.getTheme());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}

class ThemeRequest {
    private String theme;
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
}
