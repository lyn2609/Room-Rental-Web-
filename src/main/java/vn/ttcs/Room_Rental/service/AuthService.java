package vn.ttcs.Room_Rental.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.ttcs.Room_Rental.domain.Role;
import vn.ttcs.Room_Rental.domain.User;
import vn.ttcs.Room_Rental.domain.dto.ForgotPasswordRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.LoginRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.RegisterRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.ResetPasswordRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.TokenResponseDTO;
import vn.ttcs.Room_Rental.domain.dto.VerifyOtpRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.ResendVerifyOtpRequestDTO;
import vn.ttcs.Room_Rental.repository.RoleRepository;
import vn.ttcs.Room_Rental.repository.UserRepository;
import vn.ttcs.Room_Rental.security.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import vn.ttcs.Room_Rental.domain.RefreshToken;
import vn.ttcs.Room_Rental.domain.dto.RefreshTokenRequestDTO;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       OtpService otpService,
                       EmailService emailService,
                       JwtUtil jwtUtil,
                       RefreshTokenService refreshTokenService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public void register(RegisterRequestDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword()))
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");

        if (userRepository.existsByPhone(dto.getPhone()))
            throw new IllegalArgumentException("Số điện thoại đã được đăng ký");

        if (userRepository.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("Email đã được sử dụng");

        if (userRepository.existsByCccd(dto.getCccd()))
            throw new IllegalArgumentException("CCCD đã được đăng ký");

        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Role CLIENT chưa được khởi tạo trong DB"));

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setCccd(dto.getCccd());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(clientRole);
        user.setStatus("INACTIVE");
        user.setEmailVerified(false);

        userRepository.save(user);


        String otp = otpService.generateAndSave(dto.getEmail(), "VERIFY");
        emailService.sendVerificationOtp(dto.getEmail(), otp);
    }

    @Transactional
    public void verifyEmail(VerifyOtpRequestDTO dto) {
        if (!otpService.verify(dto.getEmail(), dto.getOtp(), "VERIFY"))
            throw new IllegalArgumentException("OTP không đúng hoặc đã hết hạn");

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        user.setEmailVerified(true);
        user.setStatus("ACTIVE");
        userRepository.save(user);
    }

    public void resendVerifyOtp(ResendVerifyOtpRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email chưa được đăng ký trong hệ thống"));

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Tài khoản đã được xác thực email");
        }

        String otp = otpService.generateAndSave(dto.getEmail(), "VERIFY");
        emailService.sendVerificationOtp(dto.getEmail(), otp);
    }


    public void sendResetOtp(ForgotPasswordRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email chưa được đăng ký trong hệ thống"));

        if (!user.isEmailVerified())
            throw new IllegalArgumentException("Tài khoản chưa xác thực email");

        String otp = otpService.generateAndSave(dto.getEmail(), "RESET");
        emailService.sendResetPasswordOtp(dto.getEmail(), otp);
    }

    public void resetPassword(ResetPasswordRequestDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");

        if (!otpService.verify(dto.getEmail(), dto.getOtp(), "RESET"))
            throw new IllegalArgumentException("OTP không đúng hoặc đã hết hạn");

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    public TokenResponseDTO login(LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getPhone(), dto.getPassword()));

        User user = userRepository.findByPhone(dto.getPhone())
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại"));

        if (!user.isEmailVerified())
            throw new IllegalArgumentException("Vui lòng xác nhận email trước khi đăng nhập");

        if (!"ACTIVE".equals(user.getStatus()))
            throw new IllegalArgumentException("Tài khoản đã bị khóa");

        String accessToken = jwtUtil.generateAccessToken(
                user.getPhone(), user.getRole().getName());
        RefreshToken rt = refreshTokenService.create(user);

        return new TokenResponseDTO(
                accessToken,
                rt.getToken(),
                jwtUtil.getAccessTokenExpiryMs() / 1000);
    }

    public TokenResponseDTO refreshToken(RefreshTokenRequestDTO dto) {
        RefreshToken rt = refreshTokenService.verify(dto.getRefreshToken());
        User user = rt.getUser();

        String newAccessToken = jwtUtil.generateAccessToken(
                user.getPhone(), user.getRole().getName());

        return new TokenResponseDTO(
                newAccessToken,
                rt.getToken(),
                jwtUtil.getAccessTokenExpiryMs() / 1000);
    }

    @Transactional
    public void logout(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại"));
        refreshTokenService.revokeAllByUser(user);
    }
}