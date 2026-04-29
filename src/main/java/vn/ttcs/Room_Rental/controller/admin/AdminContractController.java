package vn.ttcs.Room_Rental.controller.admin;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ttcs.Room_Rental.domain.dto.*;
import vn.ttcs.Room_Rental.service.AdminContractService; // Import Service mới
import vn.ttcs.Room_Rental.service.ContractService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/contracts")
public class AdminContractController {

    private final ContractService contractService;
    private final AdminContractService adminContractService; // Thêm Service này

    // Cập nhật Constructor để nhận cả 2 Service
    public AdminContractController(ContractService contractService, AdminContractService adminContractService) {
        this.contractService = contractService;
        this.adminContractService = adminContractService;
    }

    // --- CÁC API CŨ GIỮ NGUYÊN ---
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createContract(@Valid @RequestBody ContractCreateRequestDTO requestDTO) {
        contractService.createContract(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Tạo hợp đồng mới thành công!"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContractResponseDTO>>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) String clientName) {
        List<ContractResponseDTO> list = contractService.getAllContracts(status, roomId, clientName);
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách hợp đồng thành công", list));
    }

    // --- MỚI: API LẤY DANH SÁCH YÊU CẦU ĐANG CHỜ DUYỆT ---
    @GetMapping("/roommates/pending")
    public ResponseEntity<ApiResponse<List<RoommateResponseDTO>>> getPendingRoommates() {
        List<RoommateResponseDTO> pendingList = adminContractService.getPendingRoommates();
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách yêu cầu chờ duyệt thành công", pendingList));
    }

    // --- CẬP NHẬT: DUYỆT BẠN CÙNG PHÒNG ---
    @PatchMapping("/{id}/roommates/{rid}/approve")
    public ResponseEntity<ApiResponse<Void>> approveRoommate(
            @PathVariable("id") Integer contractId,
            @PathVariable("rid") Integer roommateId) {

        // Gọi sang service xử lý duyệt
        contractService.approveRoommate(contractId, roommateId);
        return ResponseEntity.ok(ApiResponse.ok("Đã duyệt bạn cùng phòng thành công!"));
    }

    // --- CÁC API KHÁC (UPDATE, STATUS...) GIỮ NGUYÊN ---
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateContract(@PathVariable Integer id, @RequestBody ContractUpdateRequestDTO requestDTO) {
        contractService.updateContract(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật hợp đồng thành công!"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> changeContractStatus(@PathVariable Integer id, @Valid @RequestBody ContractStatusRequestDTO requestDTO) {
        contractService.changeContractStatus(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái hợp đồng thành công!"));
    }

    @PostMapping("/{id}/roommates")
    public ResponseEntity<ApiResponse<Void>> addRoommate(@PathVariable Integer id, @RequestBody RoommateRequestDTO requestDTO) {
        contractService.addRoommateByAdmin(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.ok("Admin đã thêm người ở ghép thành công (tự động duyệt)!"));
    }
}