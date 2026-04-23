package vn.ttcs.Room_Rental.controller.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import vn.ttcs.Room_Rental.domain.dto.ApiResponse;
import vn.ttcs.Room_Rental.domain.dto.ForgotPasswordRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.LoginRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.RefreshTokenRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.RegisterRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.ResetPasswordRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.TokenResponseDTO;
import vn.ttcs.Room_Rental.domain.dto.VerifyOtpRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.ResendVerifyOtpRequestDTO;
import vn.ttcs.Room_Rental.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequestDTO dto) {
        authService.register(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        "Đăng ký thành công! Vui lòng kiểm tra email để xác nhận tài khoản."));
    }


    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @Valid @RequestBody VerifyOtpRequestDTO dto) {
        authService.verifyEmail(dto);
        return ResponseEntity.ok(ApiResponse.ok("Xác nhận email thành công!"));
    }

    @PostMapping("/resend-verify-email")
    public ResponseEntity<ApiResponse<Void>> resendVerifyEmail(
            @Valid @RequestBody ResendVerifyOtpRequestDTO dto) {
        authService.resendVerifyOtp(dto);
        return ResponseEntity.ok(ApiResponse.ok("Mã OTP mới đã được gửi đến email " + dto.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO dto) {
        TokenResponseDTO tokens = authService.login(dto);
        return ResponseEntity.ok(ApiResponse.ok("Đăng nhập thành công", tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> refresh(
            @Valid @RequestBody RefreshTokenRequestDTO dto) {
        TokenResponseDTO tokens = authService.refreshToken(dto);
        return ResponseEntity.ok(ApiResponse.ok("Cấp token mới thành công", tokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bạn chưa đăng nhập");
        }

        String phone = authentication.getName();
        authService.logout(phone);
        return ResponseEntity.ok(ApiResponse.ok("Đăng xuất thành công"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO dto) {
        authService.sendResetOtp(dto);
        return ResponseEntity.ok(ApiResponse.ok(
                "Mã OTP đã được gửi đến email " + dto.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO dto) {
        authService.resetPassword(dto);
        return ResponseEntity.ok(ApiResponse.ok("Đặt lại mật khẩu thành công!"));
    }
}