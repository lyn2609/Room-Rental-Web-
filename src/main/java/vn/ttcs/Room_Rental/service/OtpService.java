package vn.ttcs.Room_Rental.service;

import java.security.SecureRandom;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private static final String PREFIX_VERIFY = "otp:verify:";
    private static final String PREFIX_RESET  = "otp:reset:";
    private static final int    OTP_LENGTH    = 6;

    private final StringRedisTemplate redis;
    private final long expiryMinutes;

    public OtpService(StringRedisTemplate redis,
                      @Value("${otp.expiry-minutes:5}") long expiryMinutes) {
        this.redis = redis;
        this.expiryMinutes = expiryMinutes;
    }

    public String generateAndSave(String email, String type) {
        String otp = String.format("%06d", new SecureRandom().nextInt(999999));
        String key = buildKey(email, type);
        redis.opsForValue().set(key, otp, Duration.ofMinutes(expiryMinutes));
        return otp;
    }

    public boolean verify(String email, String otp, String type) {
        String key   = buildKey(email, type);
        String saved = redis.opsForValue().get(key);
        if (saved != null && saved.equals(otp)) {
            redis.delete(key);
            return true;
        }
        return false;
    }

    private String buildKey(String email, String type) {
        return switch (type) {
            case "VERIFY" -> PREFIX_VERIFY + email;
            case "RESET"  -> PREFIX_RESET  + email;
            default -> throw new IllegalArgumentException("OTP type không hợp lệ: " + type);
        };
    }
}