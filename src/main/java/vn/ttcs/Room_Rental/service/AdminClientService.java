package vn.ttcs.Room_Rental.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ttcs.Room_Rental.domain.User;
import vn.ttcs.Room_Rental.domain.dto.ClientResponseDTO;
import vn.ttcs.Room_Rental.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminClientService {

    private final UserRepository userRepository;

    public AdminClientService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. CẬP NHẬT: Hàm tìm kiếm đa điều kiện (Khớp với Controller)
    public List<ClientResponseDTO> searchClients(String fullName, String phone, String cccd, String roomName, String status) {
        // Lưu ý: Tên hàm searchClients phải khớp với tên bạn đặt trong UserRepository nhé
        List<User> users = userRepository.searchClients(fullName, phone, cccd, roomName, status);
        return users.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // 2. Lấy chi tiết bằng Integer
    public ClientResponseDTO getClientDetail(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách thuê"));
        return mapToDTO(user);
    }

    // 3. Khóa / Mở khóa tài khoản bằng Integer
    @Transactional
    public void updateClientStatus(Integer id, String newStatus) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách thuê"));

        user.setStatus(newStatus.toUpperCase());
        userRepository.save(user);
    }

    // Hàm phụ trợ chuyển User sang DTO (Giữ nguyên logic của bạn)
    private ClientResponseDTO mapToDTO(User user) {
        ClientResponseDTO dto = new ClientResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setCccd(user.getCccd());
        dto.setStatus(user.getStatus());

        if (user.getContracts() != null) {
            String roomName = user.getContracts().stream()
                    .filter(c -> "ACTIVE".equalsIgnoreCase(c.getStatus()))
                    .findFirst()
                    .map(c -> c.getRoom().getName())
                    .orElse("Chưa thuê phòng");
            dto.setRoomName(roomName);
        }

        return dto;
    }
}