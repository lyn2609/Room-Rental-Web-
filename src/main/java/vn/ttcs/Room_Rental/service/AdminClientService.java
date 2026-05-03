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

    // 1. Tìm kiếm đa điều kiện
    public List<ClientResponseDTO> searchClients(String fullName, String phone, String cccd, String roomName, String status) {
        List<User> users = userRepository.searchClients(fullName, phone, cccd, roomName, status);
        return users.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // 2. Lấy chi tiết khách thuê (Đã nâng cấp map đủ thông tin)
    public ClientResponseDTO getClientDetail(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách thuê ID: " + id));
        return mapToDTO(user);
    }

    // 3. Khóa / Mở khóa tài khoản
    @Transactional
    public void updateClientStatus(Integer id, String newStatus) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách thuê"));

        user.setStatus(newStatus.toUpperCase());
        userRepository.save(user);
    }

    // --- HÀM MAP DỮ LIỆU "LINH HỒN" CỦA FILE ---
    private ClientResponseDTO mapToDTO(User user) {
        ClientResponseDTO dto = new ClientResponseDTO();

        // Thông tin cơ bản
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setCccd(user.getCccd());
        dto.setAddress(user.getAddress());
        dto.setDob(user.getDob());
        dto.setGender(user.getGender());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());

        // Thông tin ngân hàng (Lấy từ bảng users ông vừa gửi)
        dto.setBankName(user.getBankName());
        dto.setBankAccountName(user.getBankAccountName());
        dto.setBankAccountNumber(user.getBankAccountNumber());

        // Xử lý lấy tên phòng đang thuê (Chỉ lấy hợp đồng ACTIVE)
        if (user.getContracts() != null && !user.getContracts().isEmpty()) {
            String activeRoom = user.getContracts().stream()
                    .filter(c -> "ACTIVE".equalsIgnoreCase(c.getStatus()))
                    .findFirst()
                    .map(c -> c.getRoom().getName())
                    .orElse("Chưa thuê phòng");
            dto.setRoomName(activeRoom);
        } else {
            dto.setRoomName("Chưa thuê phòng");
        }

        return dto;
    }
}