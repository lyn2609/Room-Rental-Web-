package vn.ttcs.Room_Rental.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ttcs.Room_Rental.domain.*;
import vn.ttcs.Room_Rental.domain.dto.RoommateResponseDTO;
import vn.ttcs.Room_Rental.domain.dto.ServiceRequestDTO;
import vn.ttcs.Room_Rental.domain.dto.ServiceResponseDTO;
import vn.ttcs.Room_Rental.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminContractService {

    private final RoommateRepository roommateRepository;
    private final ServiceRepository serviceRepository;
    private final RoomRepository roomRepository;

    // --- BỔ SUNG 2 REPO ĐỂ "TRUY VẾT" PHỤ LỤC ---
    private final ContractRepository contractRepository;
    private final ContractAppendixRepository appendixRepository;

    public AdminContractService(RoommateRepository roommateRepository,
                                ServiceRepository serviceRepository,
                                RoomRepository roomRepository,
                                ContractRepository contractRepository,
                                ContractAppendixRepository appendixRepository) {
        this.roommateRepository = roommateRepository;
        this.serviceRepository = serviceRepository;
        this.roomRepository = roomRepository;
        this.contractRepository = contractRepository;
        this.appendixRepository = appendixRepository;
    }

    // ======================== QUẢN LÝ BẠN CÙNG PHÒNG (GIỮ NGUYÊN) ========================

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

    // ======================== QUẢN LÝ DỊCH VỤ (NÂNG CẤP) ========================

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

    @Transactional
    public void addServiceToRoom(Integer roomId, ServiceRequestDTO dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ID: " + roomId));

        vn.ttcs.Room_Rental.domain.Service service = new vn.ttcs.Room_Rental.domain.Service();
        service.setRoom(room);
        service.setName(dto.getName());
        service.setDefaultPrice(dto.getDefaultPrice());
        service.setUnit(dto.getUnit());
        service.setIsMetered(dto.getIsMetered());

        serviceRepository.save(service);
    }

    // --- HÀM TRỌNG TÂM: Cập nhật thông tin & Ghi Phụ lục ---
    @Transactional
    public void updateService(Integer sid, ServiceRequestDTO dto) {
        vn.ttcs.Room_Rental.domain.Service service = serviceRepository.findById(sid)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại ID: " + sid));

        Double oldPrice = service.getDefaultPrice();
        String oldName = service.getName();

        // 1. Cập nhật thông tin vào bảng gốc (services)
        service.setName(dto.getName());
        service.setDefaultPrice(dto.getDefaultPrice());
        service.setUnit(dto.getUnit());
        service.setIsMetered(dto.getIsMetered());
        serviceRepository.save(service);

        // 2. LOGIC PHỤ LỤC: Nếu giá thay đổi, ghi "vết" cho các hợp đồng đang thuê
        if (!oldPrice.equals(dto.getDefaultPrice())) {
            List<Contract> activeContracts = contractRepository.findByRoom_IdAndStatus(
                    service.getRoom().getId(), "ACTIVE");

            for (Contract contract : activeContracts) {
                // Kiểm tra xem khách này có thực sự đang dùng dịch vụ này không
                boolean isUsing = contract.getServices().stream()
                        .anyMatch(sd -> sd.getService().getId().equals(sid));

                if (isUsing) {
                    ContractAppendix appendix = new ContractAppendix();
                    appendix.setContract(contract);

                    // Nội dung thông báo tự động
                    String content = String.format("Hệ thống cập nhật đơn giá dịch vụ [%s]: %.0f -> %.0f (%s)",
                            oldName, oldPrice, dto.getDefaultPrice(), service.getUnit());

                    appendix.setContent(content);
                    appendixRepository.save(appendix);
                }
            }
        }
    }

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