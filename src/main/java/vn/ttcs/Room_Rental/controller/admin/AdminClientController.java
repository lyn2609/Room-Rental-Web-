package vn.ttcs.Room_Rental.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ttcs.Room_Rental.domain.dto.ApiResponse;
import vn.ttcs.Room_Rental.domain.dto.ClientResponseDTO;
import vn.ttcs.Room_Rental.domain.dto.ClientStatusUpdateRequest;
import vn.ttcs.Room_Rental.service.AdminClientService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/clients")
public class AdminClientController {

    private final AdminClientService adminClientService;

    public AdminClientController(AdminClientService adminClientService) {
        this.adminClientService = adminClientService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientResponseDTO>>> getClients(
            @RequestParam(required = false) String keyword) {
        List<ClientResponseDTO> list = adminClientService.getClients(keyword);
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách khách thuê thành công", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponseDTO>> getClientDetail(@PathVariable Integer id) {
        ClientResponseDTO detail = adminClientService.getClientDetail(id);
        return ResponseEntity.ok(ApiResponse.ok("Lấy chi tiết khách thuê thành công", detail));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateClientStatus(
            @PathVariable Integer id,
            @RequestBody ClientStatusUpdateRequest request) {
        adminClientService.updateClientStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái tài khoản thành công", null));
    }
}