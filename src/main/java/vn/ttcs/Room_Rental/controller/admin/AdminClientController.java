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
    public ResponseEntity<ApiResponse<List<ClientResponseDTO>>> getAllClients(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String cccd,
            @RequestParam(required = false) String roomName,
            @RequestParam(required = false) String status) {

        // Truyền đủ 5 tham số xuống Service
        List<ClientResponseDTO> clients = adminClientService.searchClients(fullName, phone, cccd, roomName, status);

        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách khách thuê thành công", clients));
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