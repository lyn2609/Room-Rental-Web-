package vn.ttcs.Room_Rental.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ttcs.Room_Rental.domain.BookingView;
import vn.ttcs.Room_Rental.domain.Room;
import vn.ttcs.Room_Rental.domain.User;
import vn.ttcs.Room_Rental.domain.dto.BookingRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.RoomRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.RoomResponseDTO;
import vn.ttcs.Room_Rental.domain.dto.ServiceResponseDTO;
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

    public List<RoomResponseDTO> searchRooms(String area, Double minPrice, Double maxPrice) {
        return roomRepository.searchRooms(area, minPrice, maxPrice).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public RoomResponseDTO getRoomDetail(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));
        return mapToDTO(room);
    }

    public List<RoomResponseDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void saveOrUpdateRoom(Integer id, RoomRequestDTO dto) {
        Room room;
        if (id == null) {
            room = new Room();
        } else {
            room = roomRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ID: " + id));
        }

        room.setName(dto.getName());
        room.setArea(dto.getArea());
        room.setAddress(dto.getAddress());
        room.setPrice(dto.getPrice());
        room.setStatus(dto.getStatus());
        room.setImageUrl(dto.getImageUrl());
        room.setShortDescription(dto.getShortDescription());
        room.setDetailedDescription(dto.getDetailedDescription());
        room.setMaxOccupants(dto.getMaxOccupants());

        roomRepository.save(room);
    }

    @Transactional
    public void bookRoom(Integer roomId, Integer clientId, BookingRequestDTO dto) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        User user = userRepository.findById(clientId).orElseThrow();

        BookingView booking = new BookingView();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setAppointmentTime(dto.getAppointmentTime());
        booking.setNote(dto.getNote());
        booking.setStatus("PENDING");

        bookingViewRepository.save(booking);
    }

    private RoomResponseDTO mapToDTO(Room room) {
        RoomResponseDTO dto = new RoomResponseDTO();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setArea(room.getArea());
        dto.setAddress(room.getAddress());
        dto.setPrice(room.getPrice());
        dto.setStatus(room.getStatus());
        dto.setImageUrl(room.getImageUrl());
        dto.setShortDescription(room.getShortDescription());
        dto.setDetailedDescription(room.getDetailedDescription());
        dto.setMaxOccupants(room.getMaxOccupants());

        if (room.getServices() != null) {
            List<ServiceResponseDTO> services = room.getServices().stream()
                    .map(s -> new ServiceResponseDTO(
                            s.getId(),
                            s.getName(),
                            s.getDefaultPrice(),
                            s.getUnit(),
                            s.getIsMetered()
                    ))
                    .collect(Collectors.toList());
            dto.setServices(services);
        }

        return dto;
    }
}
