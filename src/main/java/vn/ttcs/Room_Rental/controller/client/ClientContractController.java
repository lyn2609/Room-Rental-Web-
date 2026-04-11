package vn.ttcs.Room_Rental.controller.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ttcs.Room_Rental.domain.Roommate;
import vn.ttcs.Room_Rental.domain.dto.ApiResponse;
import vn.ttcs.Room_Rental.domain.dto.ContractResponseDTO;
import vn.ttcs.Room_Rental.domain.dto.RoommateRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.RoommateResponseDTO;
import vn.ttcs.Room_Rental.service.ContractService;

import java.util.List;

@RestController
@RequestMapping("/api/client/contracts")
// @RequiredArgsConstructor
public class ClientContractController {

    private final ContractService contractService;
    public ClientContractController(ContractService contractService) {
        this.contractService = contractService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContractResponseDTO>>> getMyContracts() {
        List<ContractResponseDTO> contracts = contractService.getMyContracts();
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách hợp đồng thành công", contracts));
    }

    // Thêm người ở ghép (Cái API bạn đang test nãy giờ)
    @PostMapping("/{id}/roommates")
    public ResponseEntity<ApiResponse<Void>> addRoommate(
            @PathVariable Integer id,
            @RequestBody RoommateRequestDTO requestDTO) {

        contractService.addRoommate(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.ok("Thêm người ở ghép thành công, chờ Admin duyệt!"));
    }

    // 3. GET /api/client/contracts/{id}/roommates - Xem danh sách bạn cùng phòng
    @GetMapping("/{id}/roommates")
    public ResponseEntity<ApiResponse<List<RoommateResponseDTO>>> getRoommates(@PathVariable Integer id) {
        List<RoommateResponseDTO> list = contractService.getMyRoommates(id);
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách thành công", list));
    }

    // 5. DELETE /api/client/contracts/{id}/roommates/{rid} - Xóa bạn cùng phòng
    @DeleteMapping("/{id}/roommates/{rid}")
    public ResponseEntity<ApiResponse<Void>> deleteRoommate(
            @PathVariable("id") Integer id,
            @PathVariable("rid") Integer rid) {

        contractService.deleteRoommate(id, rid);
        return ResponseEntity.ok(ApiResponse.ok("Đã xóa bạn cùng phòng thành công!"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractResponseDTO>> getContractDetail(@PathVariable Integer id) {
        ContractResponseDTO detail = contractService.getContractDetail(id);
        return ResponseEntity.ok(ApiResponse.ok("Lấy chi tiết hợp đồng thành công", detail));
    }
}