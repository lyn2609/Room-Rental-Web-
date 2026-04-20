package vn.ttcs.Room_Rental.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ttcs.Room_Rental.domain.dto.GenerateInvoiceRequest;
import vn.ttcs.Room_Rental.domain.dto.InvoiceDashboardResponse;
import vn.ttcs.Room_Rental.domain.dto.InvoiceResponse;
import vn.ttcs.Room_Rental.domain.dto.UpdateInvoiceRequest;
import vn.ttcs.Room_Rental.service.InvoiceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/invoices")
public class InvoiceAdminController {

    private final InvoiceService invoiceService;

    public InvoiceAdminController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // GET /api/admin/invoices?month=2025-06&contractId=1
    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Integer contractId) {
        return ResponseEntity.ok(invoiceService.getAllInvoices(month, contractId));
    }

    // POST /api/admin/invoices/generate
    @PostMapping("/generate")
    public ResponseEntity<List<InvoiceResponse>> generateInvoices(
            @Valid @RequestBody GenerateInvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.generateInvoices(request));
    }

    // PATCH /api/admin/invoices/{id}/confirm-cash
    @PatchMapping("/{id}/confirm-cash")
    public ResponseEntity<InvoiceResponse> confirmCash(@PathVariable Integer id) {
        return ResponseEntity.ok(invoiceService.confirmCashPayment(id));
    }

    // PATCH /api/admin/invoices/contract/{contractId}/month/{month}
    @PatchMapping("/contract/{contractId}/month/{month}")
    public ResponseEntity<InvoiceResponse> updateInvoiceByContractAndMonth(
            @PathVariable Integer contractId,
            @PathVariable String month,
            @Valid @RequestBody UpdateInvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.updateInvoiceByContractAndMonth(contractId, month, request));
    }

    // GET /api/admin/invoices/dashboard?month=2025-06
    @GetMapping("/dashboard")
    public ResponseEntity<InvoiceDashboardResponse> getDashboard(
            @RequestParam(required = false) String month) {
        return ResponseEntity.ok(invoiceService.getDashboard(month));
    }

    // PATCH /api/admin/invoices/sync-overdue
    // Đồng bộ trạng thái OVERDUE xuống DB (gọi thủ công nếu cần)
    @PatchMapping("/sync-overdue")
    public ResponseEntity<Map<String, String>> syncOverdue() {
        invoiceService.syncOverdueStatuses();
        Map<String, String> result = new HashMap<>();
        result.put("message", "Đã đồng bộ trạng thái OVERDUE cho các hóa đơn quá hạn");
        return ResponseEntity.ok(result);
    }

    // GET /api/admin/invoices/{id}/vnpay
    @GetMapping("/{id}/vnpay")
    public ResponseEntity<Map<String, String>> createVNPayPaymentForAdmin(
            @PathVariable Integer id,
            @RequestParam(required = false) String bankCode,
            HttpServletRequest request) {

        // Admin không cần check user sở hữu hóa đơn, nên truyền null/hoặc dùng method riêng trong service
        String paymentUrl = invoiceService.createVNPayPaymentUrlForAdmin(id, getClientIp(request), bankCode);

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        response.put("selectedBankCode", bankCode != null ? bankCode : "ALL");
        response.put("message", "Tạo link thanh toán VNPAY thành công");

        return ResponseEntity.ok(response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}