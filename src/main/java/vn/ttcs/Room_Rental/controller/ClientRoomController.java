package vn.ttcs.Room_Rental.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ttcs.Room_Rental.domain.dto.ApiResponse;
import vn.ttcs.Room_Rental.domain.dto.BookingRequestDTO;
import vn.ttcs.Room_Rental.service.RoomService;

@RestController
@RequestMapping("/api/client/rooms")
public class ClientRoomController {

    private final RoomService roomService;

    public ClientRoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Đặt lịch xem phòng trực tiếp
    @PostMapping("/{id}/booking")
    public ResponseEntity<ApiResponse<Void>> bookRoom(
            @PathVariable Integer id,
            @RequestBody BookingRequestDTO dto) {

        // Giả sử bạn đã có cách lấy clientId từ SecurityContext (Token)
        // Ở đây mình ví dụ truyền cứng hoặc lấy từ Principal
        Integer clientId = 2; // Ví dụ ID khách đang đăng nhập

        roomService.bookRoom(id, clientId, dto);
        return ResponseEntity.ok(ApiResponse.ok("Đặt lịch xem phòng thành công", null));
    }
}