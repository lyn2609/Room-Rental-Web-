package vn.ttcs.Room_Rental.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ttcs.Room_Rental.domain.BookingView;
import vn.ttcs.Room_Rental.domain.Room;
import vn.ttcs.Room_Rental.domain.User;
import vn.ttcs.Room_Rental.domain.dto.BookingRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.RoomRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.RoomResponseDTO;
import vn.ttcs.Room_Rental.repository.BookingViewRepository;
import vn.ttcs.Room_Rental.repository.RoomRepository;
import vn.ttcs.Room_Rental.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingViewRepository bookingViewRepository;

    public RoomService(RoomRepository roomRepository,
                       UserRepository userRepository,
                       BookingViewRepository bookingViewRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.bookingViewRepository = bookingViewRepository;
    }

    // 1. Tìm kiếm (Public)
    public List<RoomResponseDTO> searchRooms(String area, Double minPrice, Double maxPrice) {
        return roomRepository.searchRooms(area, minPrice, maxPrice).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    // 2. Chi tiết (Public)
    public RoomResponseDTO getRoomDetail(Integer id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));
        return mapToDTO(room);
    }

    // 3. Quản lý toàn bộ (Admin)
    public List<RoomResponseDTO> getAllRooms() {
        return roomRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // 4. Thêm/Sửa (Admin) - ĐÃ CẬP NHẬT ADDRESS
    @Transactional
    public void saveOrUpdateRoom(Integer id, RoomRequestDTO dto) {
        Room room;
        if (id == null) {
            room = new Room();
        } else {
            room = roomRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));
        }

        room.setName(dto.getName());
        room.setArea(dto.getArea());
        room.setAddress(dto.getAddress()); // Dòng quan trọng nhất để fix lỗi 500
        room.setPrice(dto.getPrice());
        room.setStatus(dto.getStatus());
        room.setDescription(dto.getDescription());

        roomRepository.save(room);
    }

    // 5. Đặt lịch xem phòng (Client)
    @Transactional
    public void bookRoom(Integer roomId, Integer clientId, BookingRequestDTO dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));
        User user = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        BookingView booking = new BookingView();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setAppointmentTime(dto.getAppointmentTime());
        booking.setNote(dto.getNote());
        booking.setStatus("PENDING");

        bookingViewRepository.save(booking);
    }

    // CẬP NHẬT MAP TO DTO ĐỂ TRẢ VỀ CẢ ĐỊA CHỈ
    private RoomResponseDTO mapToDTO(Room room) {
        RoomResponseDTO dto = new RoomResponseDTO();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setArea(room.getArea());
        dto.setAddress(room.getAddress()); // Thêm dòng này để Client/Admin thấy địa chỉ
        dto.setPrice(room.getPrice());
        dto.setStatus(room.getStatus());
        dto.setDescription(room.getDescription());
        return dto;
    }
}