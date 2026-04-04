package vn.ttcs.Room_Rental.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail  = fromEmail;
    }

    @Async
    public void sendVerificationOtp(String toEmail, String otp) {
        sendOtpEmail(toEmail, otp,
            "Xác nhận tài khoản - Phòng Trọ",
            "xác nhận tài khoản");
    }

    @Async
    public void sendResetPasswordOtp(String toEmail, String otp) {
        sendOtpEmail(toEmail, otp,
            "Đặt lại mật khẩu - Phòng Trọ",
            "đặt lại mật khẩu");
    }

    private void sendOtpEmail(String toEmail, String otp, String subject, String purpose) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(buildHtmlBody(otp, purpose), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage());
        }
    }

    private String buildHtmlBody(String otp, String purpose) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto;
                        border: 1px solid #e0e0e0; border-radius: 8px; padding: 32px;">
                <h2 style="color: #2d6cdf; margin-bottom: 8px;">Hệ thống quản lý phòng trọ</h2>
                <p>Mã OTP để <strong>%s</strong> của bạn là:</p>
                <div style="font-size: 36px; font-weight: bold; letter-spacing: 8px;
                            color: #2d6cdf; text-align: center; padding: 16px 0;">
                    %s
                </div>
                <p style="color: #888; font-size: 13px;">
                    Mã có hiệu lực trong <strong>5 phút</strong>.<br>
                    Không chia sẻ mã này với bất kỳ ai.
                </p>
            </div>
            """.formatted(purpose, otp);
    }
}
