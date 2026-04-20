package vn.ttcs.Room_Rental.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ttcs.Room_Rental.domain.dto.ApiResponse;
import vn.ttcs.Room_Rental.domain.dto.InvoiceResponse;
import vn.ttcs.Room_Rental.service.InvoiceService;

@RestController
@RequestMapping("/api/vnpay")
public class VNPayController {

    private final InvoiceService invoiceService;

    public VNPayController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/return")
    public ResponseEntity<ApiResponse<InvoiceResponse>> handleReturn(
            @RequestParam Map<String, String> params) {
        InvoiceResponse invoice = invoiceService.confirmVNPayReturn(params);
        return ResponseEntity.ok(ApiResponse.ok("Thanh toán VNPAY thành công", invoice));
    }

    @GetMapping("/ipn")
    public ResponseEntity<Map<String, String>> handleIpn(
            @RequestParam Map<String, String> params) {
        Map<String, String> response = new HashMap<>();

        try {
            invoiceService.confirmVNPayReturn(params);
            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");
        } catch (IllegalArgumentException ex) {
            response.put("RspCode", "01");
            response.put("Message", ex.getMessage());
        } catch (RuntimeException ex) {
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
        }

        return ResponseEntity.ok(response);
    }
}
