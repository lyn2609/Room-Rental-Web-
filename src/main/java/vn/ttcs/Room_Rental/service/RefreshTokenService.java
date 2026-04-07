package vn.ttcs.Room_Rental.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ttcs.Room_Rental.domain.RefreshToken;
import vn.ttcs.Room_Rental.domain.User;
import vn.ttcs.Room_Rental.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final long expiryDays;

    public RefreshTokenService(RefreshTokenRepository repo,
                               @Value("${jwt.refresh-token-expiry-days}") long expiryDays) {
        this.repo       = repo;
        this.expiryDays = expiryDays;
    }

    @Transactional
    public RefreshToken create(User user) {
        repo.revokeAllByUser(user);

        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID().toString());
        rt.setUser(user);
        rt.setExpiresAt(Instant.now().plusSeconds(expiryDays * 24 * 3600));
        return repo.save(rt);
    }

    public RefreshToken verify(String token) {
        RefreshToken rt = repo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token không hợp lệ"));

        if (rt.isRevoked())
            throw new IllegalArgumentException("Refresh token đã bị thu hồi");

        if (rt.getExpiresAt().isBefore(Instant.now())) {
            rt.setRevoked(true);
            repo.save(rt);
            throw new IllegalArgumentException("Refresh token đã hết hạn, vui lòng đăng nhập lại");
        }

        return rt;
    }

    @Transactional
    public void revokeAllByUser(User user) {
        repo.revokeAllByUser(user);
    }
}