package vn.ttcs.Room_Rental.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ttcs.Room_Rental.domain.Room;
import vn.ttcs.Room_Rental.domain.Roommate;
import vn.ttcs.Room_Rental.domain.dto.RoommateResponseDTO;
import vn.ttcs.Room_Rental.domain.dto.ServiceRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.ServiceResponseDTO;
import vn.ttcs.Room_Rental.repository.RoomRepository;
import vn.ttcs.Room_Rental.repository.RoommateRepository;
import vn.ttcs.Room_Rental.repository.ServiceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminContractService {

    private final RoommateRepository roommateRepository;
    private final ServiceRepository serviceRepository;
    private final RoomRepository roomRepository;

    public AdminContractService(RoommateRepository roommateRepository,
                                ServiceRepository serviceRepository,
                                RoomRepository roomRepository) {
        this.roommateRepository = roommateRepository;
        this.serviceRepository = serviceRepository;
        this.roomRepository = roomRepository;
    }

    // ======================== QUẢN LÝ BẠN CÙNG PHÒNG (CŨ) ========================

    public List<RoommateResponseDTO> getPendingRoommates() {
        return roommateRepository.findByStatus("PENDING").stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveRoommate(Integer id, String status) {
        Roommate roommate = roommateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu bạn cùng phòng"));

        roommate.setStatus(status.toUpperCase());
        roommateRepository.save(roommate);
    }

    // ======================== QUẢN LÝ DỊCH VỤ (MỚI) ========================

    // 1. GET: Xem các dịch vụ của 1 phòng
    public List<ServiceResponseDTO> getServicesByRoom(Integer roomId) {
        return serviceRepository.findByRoom_Id(roomId).stream()
                .map(s -> new ServiceResponseDTO(
                        s.getId(),
                        s.getName(),
                        s.getDefaultPrice(),
                        s.getUnit(),
                        s.getIsMetered()))
                .collect(Collectors.toList());
    }

    // 2. POST: Thêm dịch vụ mới cho phòng
    @Transactional
    public void addServiceToRoom(Integer roomId, ServiceRequestDTO dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ID: " + roomId));

        // Dùng đường dẫn đầy đủ để tránh xung đột với @Service của Spring
        vn.ttcs.Room_Rental.domain.Service service = new vn.ttcs.Room_Rental.domain.Service();
        service.setRoom(room);
        service.setName(dto.getName());
        service.setDefaultPrice(dto.getDefaultPrice());
        service.setUnit(dto.getUnit());
        service.setIsMetered(dto.getIsMetered());

        serviceRepository.save(service);
    }

    // 3. PUT: Cập nhật thông tin dịch vụ
    @Transactional
    public void updateService(Integer sid, ServiceRequestDTO dto) {
        vn.ttcs.Room_Rental.domain.Service service = serviceRepository.findById(sid)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại ID: " + sid));

        service.setName(dto.getName());
        service.setDefaultPrice(dto.getDefaultPrice());
        service.setUnit(dto.getUnit());
        service.setIsMetered(dto.getIsMetered());

        serviceRepository.save(service);
    }

    // 4. DELETE: Xóa dịch vụ khỏi phòng
    @Transactional
    public void deleteService(Integer sid) {
        if (!serviceRepository.existsById(sid)) {
            throw new RuntimeException("Dịch vụ không tồn tại");
        }
        serviceRepository.deleteById(sid);
    }

    // ======================== MAPPING DTO ========================

    private RoommateResponseDTO mapToDTO(Roommate r) {
        RoommateResponseDTO dto = new RoommateResponseDTO();
        dto.setId(r.getId());
        dto.setFullName(r.getFullName());
        dto.setPhone(r.getPhone());
        dto.setStatus(r.getStatus());
        dto.setCccd(r.getCccd());
        dto.setGender(r.getGender());
        dto.setHometown(r.getHometown());

        if (r.getContract() != null) {
            dto.setContractId(r.getContract().getId());
            if (r.getContract().getRoom() != null) {
                dto.setRoomName(r.getContract().getRoom().getName());
            }
        }
        return dto;
    }
}