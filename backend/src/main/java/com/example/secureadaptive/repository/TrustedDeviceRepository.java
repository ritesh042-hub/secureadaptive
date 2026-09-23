package com.example.secureadaptive.repository;

import com.example.secureadaptive.entity.TrustedDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, Long> {
    List<TrustedDevice> findByUserIdAndRevokedFalse(Long userId);
    Optional<TrustedDevice> findByUserIdAndDeviceTokenHashAndRevokedFalse(Long userId, String deviceTokenHash);
}
