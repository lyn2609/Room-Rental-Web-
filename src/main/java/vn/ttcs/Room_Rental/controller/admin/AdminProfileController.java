package vn.ttcs.Room_Rental.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ttcs.Room_Rental.domain.dto.AdminVehicleResponse;
import vn.ttcs.Room_Rental.domain.dto.ApiResponse;
import vn.ttcs.Room_Rental.service.AdminProfileService;

@RestController
@RequestMapping("/api/admin/profile")
public class AdminProfileController {

    private final AdminProfileService adminProfileService;

    public AdminProfileController(AdminProfileService adminProfileService) {
        this.adminProfileService = adminProfileService;
    }

    @GetMapping("/vehicles")
    public ResponseEntity<ApiResponse<List<AdminVehicleResponse>>> getAllVehicles() {
        List<AdminVehicleResponse> vehicles = adminProfileService.getAllVehicles();
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách xe thành công", vehicles));
    }

    @PatchMapping("/vehicles/{id}/status")
    public ResponseEntity<ApiResponse<AdminVehicleResponse>> updateVehicleStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }
        AdminVehicleResponse updatedVehicle = adminProfileService.updateVehicleStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái duyệt xe thành công", updatedVehicle));
    }
}
