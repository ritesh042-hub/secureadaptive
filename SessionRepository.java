package com.example.secureadaptive.repository;

import com.example.secureadaptive.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByUserIdAndRevokedFalse(Long userId);
}
