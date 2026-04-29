package vn.ttcs.Room_Rental.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ttcs.Room_Rental.domain.Roommate;
import vn.ttcs.Room_Rental.domain.dto.ApiResponse;
import vn.ttcs.Room_Rental.domain.dto.RoommateResponseDTO;
import vn.ttcs.Room_Rental.repository.RoommateRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminContractService {

    private final RoommateRepository roommateRepository;

    public AdminContractService(RoommateRepository roommateRepository) {
        this.roommateRepository = roommateRepository;
    }

    // 1. Lấy danh sách tất cả yêu cầu bạn cùng phòng đang PENDING
    public List<RoommateResponseDTO> getPendingRoommates() {
        return roommateRepository.findByStatus("PENDING").stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 2. Duyệt hoặc Từ chối bạn cùng phòng
    @Transactional
    public void approveRoommate(Integer id, String status) {
        Roommate roommate = roommateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu bạn cùng phòng"));

        roommate.setStatus(status.toUpperCase()); // Ví dụ: APPROVED hoặc REJECTED
        roommateRepository.save(roommate);
    }

    // Hàm map sang DTO
    private RoommateResponseDTO mapToDTO(Roommate r) {
        RoommateResponseDTO dto = new RoommateResponseDTO();
        dto.setId(r.getId());
        dto.setFullName(r.getFullName());
        dto.setPhone(r.getPhone());
        dto.setStatus(r.getStatus());
        dto.setCccd(r.getCccd());
        dto.setGender(r.getGender());
        dto.setHometown(r.getHometown());
        // Nếu có quan hệ với Contract thì lấy thêm tên phòng ở đây
        if (r.getContract() != null) {
            dto.setContractId(r.getContract().getId()); // Fix null contractId

            if (r.getContract().getRoom() != null) {
                dto.setRoomName(r.getContract().getRoom().getName());
            }
        }
        return dto;
    }
}