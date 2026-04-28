package vn.ttcs.Room_Rental.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ttcs.Room_Rental.domain.dto.ApiResponse;
import vn.ttcs.Room_Rental.domain.dto.RoomRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.RoomResponseDTO;
import vn.ttcs.Room_Rental.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/rooms")
public class AdminRoomController {

    private final RoomService roomService;

    public AdminRoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Admin xem tất cả các phòng (kể cả phòng đang thuê hay bảo trì)
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponseDTO>>> getAllRooms() {
        List<RoomResponseDTO> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(ApiResponse.ok("Admin lấy toàn bộ danh sách phòng thành công", rooms));
    }

    // Thêm phòng mới
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createRoom(@RequestBody RoomRequestDTO dto) {
        roomService.saveOrUpdateRoom(null, dto);
        return ResponseEntity.ok(ApiResponse.ok("Thêm phòng mới thành công", null));
    }

    // Cập nhật thông tin phòng hiện có
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateRoom(
            @PathVariable Integer id,
            @RequestBody RoomRequestDTO dto) {
        roomService.saveOrUpdateRoom(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật thông tin phòng thành công", null));
    }
}