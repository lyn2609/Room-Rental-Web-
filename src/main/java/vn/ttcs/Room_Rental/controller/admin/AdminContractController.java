package vn.ttcs.Room_Rental.controller.admin;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ttcs.Room_Rental.domain.dto.*;
import vn.ttcs.Room_Rental.service.ContractService;
import java.util.List;


@RestController
@RequestMapping("/api/admin/contracts")
// @RequiredArgsConstructor
public class AdminContractController {

    private final ContractService contractService;
    public AdminContractController(ContractService contractService) {
        this.contractService = contractService;
    }


    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createContract(
            @Valid @RequestBody ContractCreateRequestDTO requestDTO) {

        contractService.createContract(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo hợp đồng mới thành công!"));
    }

    // 2. GET /api/admin/contracts - Lấy danh sách toàn bộ hợp đồng
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContractResponseDTO>>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) String clientName) {

        List<ContractResponseDTO> list = contractService.getAllContracts(status, roomId, clientName);

        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách hợp đồng thành công", list));
    }

    // 3. PUT /api/admin/contracts/{id} - Cập nhật hợp đồng
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateContract(
            @PathVariable Integer id,
            @RequestBody ContractUpdateRequestDTO requestDTO) {

        contractService.updateContract(id, requestDTO);

        return ResponseEntity.ok(ApiResponse.ok("Cập nhật hợp đồng thành công!"));
    }

    // 4. PATCH /api/admin/contracts/{id}/status - Thay đổi trạng thái (Thanh lý/Kích hoạt)
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> changeContractStatus(
            @PathVariable Integer id,
            @Valid @RequestBody ContractStatusRequestDTO requestDTO) {

        contractService.changeContractStatus(id, requestDTO);

        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái hợp đồng thành công!"));
    }

    // POST /api/admin/contracts/{id}/roommates - Admin thêm người ở ghép (không cần check ownership)
    @PostMapping("/{id}/roommates")
    public ResponseEntity<ApiResponse<Void>> addRoommate(
            @PathVariable Integer id,
            @RequestBody RoommateRequestDTO requestDTO) {

        contractService.addRoommateByAdmin(id, requestDTO);

        return ResponseEntity.ok(ApiResponse.ok("Admin đã thêm người ở ghép thành công (tự động duyệt)!"));
    }

    // 10. PATCH /api/admin/contracts/{id}/roommates/{rid}/approve - Duyệt bạn cùng phòng
    @PatchMapping("/{id}/roommates/{rid}/approve")
    public ResponseEntity<ApiResponse<Void>> approveRoommate(
            @PathVariable("id") Integer contractId,
            @PathVariable("rid") Integer roommateId) {

        contractService.approveRoommate(contractId, roommateId);

        return ResponseEntity.ok(ApiResponse.ok("Đã duyệt bạn cùng phòng thành công!"));
    }
}