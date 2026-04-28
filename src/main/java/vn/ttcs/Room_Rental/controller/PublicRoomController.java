package vn.ttcs.Room_Rental.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ttcs.Room_Rental.domain.dto.ApiResponse;
import vn.ttcs.Room_Rental.domain.dto.RoomResponseDTO;
import vn.ttcs.Room_Rental.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class PublicRoomController {

    private final RoomService roomService;

    public PublicRoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Lấy danh sách phòng trống + Tìm kiếm theo khu vực, giá
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponseDTO>>> searchRooms(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        List<RoomResponseDTO> rooms = roomService.searchRooms(area, minPrice, maxPrice);
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách phòng thành công", rooms));
    }

    // Xem chi tiết một phòng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponseDTO>> getRoomDetail(@PathVariable Integer id) {
        RoomResponseDTO room = roomService.getRoomDetail(id);
        return ResponseEntity.ok(ApiResponse.ok("Lấy chi tiết phòng thành công", room));
    }
}