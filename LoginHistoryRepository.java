package com.example.secureadaptive.repository;

import com.example.secureadaptive.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findByUserIdOrderByLoginTimestampDesc(Long userId);
    List<LoginHistory> findTop1ByUserIdOrderByLoginTimestampDesc(Long userId);
}
