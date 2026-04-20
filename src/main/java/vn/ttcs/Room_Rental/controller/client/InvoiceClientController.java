package vn.ttcs.Room_Rental.controller.client;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import vn.ttcs.Room_Rental.domain.User;
import vn.ttcs.Room_Rental.domain.dto.InvoiceResponse;
import vn.ttcs.Room_Rental.repository.UserRepository;
import vn.ttcs.Room_Rental.service.InvoiceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/client/invoices")
public class InvoiceClientController {

    private final InvoiceService invoiceService;
    private final UserRepository userRepository;

    public InvoiceClientController(InvoiceService invoiceService, UserRepository userRepository) {
        this.invoiceService = invoiceService;
        this.userRepository = userRepository;
    }

    // GET /api/client/invoices
    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getMyInvoices(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(invoiceService.getMyInvoices(user.getId()));
    }

    // GET /api/client/invoices/{id}
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceDetail(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(invoiceService.getMyInvoiceDetail(id, user.getId()));
    }

    // GET /api/client/invoices/{id}/vnpay
    @GetMapping("/{id}/vnpay")
    public ResponseEntity<Map<String, String>> createVNPayPayment(
            @PathVariable Integer id,
            @RequestParam(required = false) String bankCode,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {
        User user = getCurrentUser(userDetails);

        String paymentUrl = invoiceService.createVNPayPaymentUrl(id, user.getId(), getClientIp(request), bankCode);

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        response.put("selectedBankCode", bankCode != null ? bankCode : "ALL");
        response.put("message", "Tạo link thanh toán VNPAY thành công");

        return ResponseEntity.ok(response);
    }

    private User getCurrentUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Bạn chưa đăng nhập");
        }

        return userRepository.findByPhone(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Người dùng không hợp lệ"));
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}