package vn.ttcs.Room_Rental.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ttcs.Room_Rental.domain.dto.ServiceRequestDTO;
import vn.ttcs.Room_Rental.service.AdminContractService;

@RestController
@RequestMapping("/api/admin")
public class AdminServiceController {

    private final AdminContractService adminContractService;

    public AdminServiceController(AdminContractService adminContractService) {
        this.adminContractService = adminContractService;
    }

    @GetMapping("/rooms/{id}/services")
    public ResponseEntity<?> getServices(@PathVariable Integer id) {
        return ResponseEntity.ok(adminContractService.getServicesByRoom(id));
    }

    @PostMapping("/rooms/{id}/services")
    public ResponseEntity<?> addService(@PathVariable Integer id, @RequestBody ServiceRequestDTO dto) {
        adminContractService.addServiceToRoom(id, dto);
        return ResponseEntity.ok("Thêm dịch vụ thành công");
    }

    @PutMapping("/services/{sid}")
    public ResponseEntity<?> updateService(@PathVariable Integer sid, @RequestBody ServiceRequestDTO dto) {
        adminContractService.updateService(sid, dto);
        return ResponseEntity.ok("Cập nhật thành công");
    }

    @DeleteMapping("/services/{sid}")
    public ResponseEntity<?> deleteService(@PathVariable Integer sid) {
        adminContractService.deleteService(sid);
        return ResponseEntity.ok("Xóa thành công");
    }
}