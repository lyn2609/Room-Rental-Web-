package vn.ttcs.Room_Rental.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.ttcs.Room_Rental.domain.*;
import vn.ttcs.Room_Rental.domain.dto.*;
import vn.ttcs.Room_Rental.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoommateRepository roommateRepository;
    private final ContractAppendixRepository appendixRepository;
    private final ServiceRepository serviceRepository;

    public ContractServiceImpl(ContractRepository contractRepository,
                               RoomRepository roomRepository,
                               UserRepository userRepository,
                               RoommateRepository roommateRepository,
                               ContractAppendixRepository appendixRepository,
                               ServiceRepository serviceRepository) {
        this.contractRepository = contractRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roommateRepository = roommateRepository;
        this.appendixRepository = appendixRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    @Transactional
    public void createContract(ContractCreateRequestDTO dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));

        if (!"AVAILABLE".equals(room.getStatus())) {
            throw new RuntimeException("Phòng này hiện không trống để cho thuê!");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Contract contract = new Contract();
        contract.setUser(user);
        contract.setRoom(room);
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        contract.setDeposit(resolveDepositFromRoom(room));
        contract.setStatus("ACTIVE");

        // --- TỰ ĐỘNG CHỐT DỊCH VỤ VÀ GIÁ (Snapshot Price) ---
        if (room.getServices() != null && !room.getServices().isEmpty()) {
            List<ContractServiceDetail> details = room.getServices().stream().map(s -> {
                ContractServiceDetail detail = new ContractServiceDetail();
                detail.setContract(contract);
                detail.setService(s);
                detail.setActualPrice(s.getDefaultPrice()); // Chốt giá tại thời điểm ký
                return detail;
            }).collect(Collectors.toList());
            contract.setServices(details);
        }

        room.setStatus("RENTED");
        roomRepository.save(room);
        contractRepository.save(contract);
    }

    @Override
    public List<ContractResponseDTO> getAllContracts(String status, Integer roomId, String clientName) {
        List<Contract> contracts = contractRepository.findWithFilters(status, roomId, clientName);
        return contracts.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateContract(Integer id, ContractUpdateRequestDTO dto) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng số: " + id));

        StringBuilder changes = new StringBuilder("Cập nhật hợp đồng: ");
        boolean isChanged = false;

        // 1. Cập nhật Ngày kết thúc
        if (dto.getEndDate() != null && !dto.getEndDate().equals(contract.getEndDate())) {
            changes.append("[Hạn cũ: ").append(contract.getEndDate()).append(" -> Mới: ").append(dto.getEndDate()).append("] ");
            contract.setEndDate(dto.getEndDate());
            isChanged = true;
        }

        // 2. Cập nhật Tiền cọc
        if (dto.getDeposit() != null && !dto.getDeposit().equals(contract.getDeposit())) {
            changes.append("[Cọc cũ: ").append(contract.getDeposit()).append(" -> Mới: ").append(dto.getDeposit()).append("] ");
            contract.setDeposit(dto.getDeposit());
            isChanged = true;
        }

        // 3. Cập nhật danh sách dịch vụ & Chốt giá mới
        if (dto.getServiceIds() != null) {
            List<vn.ttcs.Room_Rental.domain.Service> selectedServices = serviceRepository.findAllById(dto.getServiceIds());

            // Xóa danh sách cũ (orphanRemoval = true sẽ tự xóa trong DB)
            contract.getServices().clear();

            // Thêm danh sách mới với giá hiện tại
            List<ContractServiceDetail> newDetails = selectedServices.stream().map(s -> {
                ContractServiceDetail detail = new ContractServiceDetail();
                detail.setContract(contract);
                detail.setService(s);
                detail.setActualPrice(s.getDefaultPrice());
                return detail;
            }).collect(Collectors.toList());

            contract.getServices().addAll(newDetails);

            String names = selectedServices.stream().map(vn.ttcs.Room_Rental.domain.Service::getName).collect(Collectors.joining(", "));
            changes.append("[Dịch vụ mới chọn: ").append(names).append("] ");
            isChanged = true;
        }

        if (isChanged) {
            ContractAppendix appendix = new ContractAppendix();
            appendix.setContract(contract);
            String note = (dto.getNote() != null) ? "\nGhi chú: " + dto.getNote() : "";
            appendix.setContent(changes.toString() + note);
            appendixRepository.save(appendix);
            contractRepository.save(contract);
        }
    }

    @Override
    @Transactional
    public void updateServicePrice(Integer serviceId, Double newPrice) {
        vn.ttcs.Room_Rental.domain.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ"));

        Double oldPrice = service.getDefaultPrice();
        if (oldPrice.equals(newPrice)) return;

        // Cập nhật bảng gốc (cho các HĐ tương lai)
        service.setDefaultPrice(newPrice);
        serviceRepository.save(service);

        // Ghi phụ lục cho các hợp đồng ACTIVE đang dùng dịch vụ này
        List<Contract> activeContracts = contractRepository.findByRoom_IdAndStatus(service.getRoom().getId(), "ACTIVE");
        for (Contract contract : activeContracts) {
            // Kiểm tra xem HĐ này có đang dùng dịch vụ này không
            boolean isUsing = contract.getServices().stream()
                    .anyMatch(sd -> sd.getService().getId().equals(serviceId));

            if (isUsing) {
                ContractAppendix appendix = new ContractAppendix();
                appendix.setContract(contract);
                appendix.setContent("Hệ thống cập nhật giá dịch vụ [" + service.getName() + "]: "
                        + oldPrice + " -> " + newPrice + " (" + service.getUnit() + ")");
                appendixRepository.save(appendix);
            }
        }
    }

    // Hàm bổ trợ để map dữ liệu sang DTO (Dùng cho getAll và getDetail)
    private ContractResponseDTO mapToResponseDTO(Contract c) {
        ContractResponseDTO dto = new ContractResponseDTO();
        dto.setId(c.getId());
        dto.setRoomId(c.getRoom().getId());
        dto.setRoomName(c.getRoom().getName());
        dto.setUserId(c.getUser().getId());
        dto.setUserFullName(c.getUser().getFullName());
        dto.setStartDate(c.getStartDate());
        dto.setEndDate(c.getEndDate());
        dto.setDeposit(c.getDeposit());
        dto.setStatus(c.getStatus());

        if (c.getServices() != null) {
            List<ServiceResponseDTO> serviceDTOs = c.getServices().stream()
                    .map(sd -> new ServiceResponseDTO(
                            sd.getService().getId(),
                            sd.getService().getName(),
                            sd.getActualPrice(), // Lấy giá ĐÃ CHỐT trong HĐ
                            sd.getService().getUnit(),
                            sd.getService().getIsMetered()
                    ))
                    .collect(Collectors.toList());
            dto.setServices(serviceDTOs);
        }
        return dto;
    }

    @Override
    public List<ContractAppendixResponseDTO> getContractAppendices(Integer contractId) {
        return appendixRepository.findByContractIdOrderByCreatedAtDesc(contractId)
                .stream()
                .map(app -> new ContractAppendixResponseDTO(app.getId(), app.getContent(), app.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void changeContractStatus(Integer id, ContractStatusRequestDTO dto) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng số: " + id));

        String newStatus = dto.getStatus().toUpperCase();
        contract.setStatus(newStatus);

        Room room = contract.getRoom();
        if ("TERMINATED".equals(newStatus) || "EXPIRED".equals(newStatus)) {
            boolean hasOtherActiveContract = contractRepository.existsByRoom_IdAndStatusAndIdNot(
                    room.getId(), "ACTIVE", contract.getId());
            room.setStatus(hasOtherActiveContract ? "RENTED" : "AVAILABLE");
        } else if ("ACTIVE".equals(newStatus)) {
            room.setStatus("RENTED");
        }

        roomRepository.save(room);
        contractRepository.save(contract);
    }

    @Override
    public void addRoommate(Integer contractId, RoommateRequestDTO dto) {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByPhone(phone).orElseThrow();

        Contract contract = contractRepository.findById(contractId).orElseThrow();
        if (!contract.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền!");
        }

        Roommate roommate = new Roommate();
        roommate.setContract(contract);
        roommate.setFullName(dto.getFullName());
        roommate.setCccd(dto.getCccd());
        roommate.setPhone(dto.getPhone());
        roommate.setGender(dto.getGender());
        roommate.setHometown(dto.getHometown());
        roommate.setStatus("PENDING");
        roommateRepository.save(roommate);
    }

    @Override
    public void addRoommateByAdmin(Integer contractId, RoommateRequestDTO dto) {
        Contract contract = contractRepository.findById(contractId).orElseThrow();
        Roommate roommate = new Roommate();
        roommate.setContract(contract);
        roommate.setFullName(dto.getFullName());
        roommate.setCccd(dto.getCccd());
        roommate.setPhone(dto.getPhone());
        roommate.setGender(dto.getGender());
        roommate.setHometown(dto.getHometown());
        roommate.setStatus("APPROVED");
        roommateRepository.save(roommate);
    }

    @Override
    public List<ContractResponseDTO> getMyContracts() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByPhone(phone).orElseThrow();
        return contractRepository.findByUserId(user.getId())
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public void approveRoommate(Integer contractId, Integer roommateId) {
        Roommate roommate = roommateRepository.findById(roommateId).orElseThrow();
        if (!roommate.getContract().getId().equals(contractId)) {
            throw new RuntimeException("Dữ liệu không đồng nhất!");
        }
        roommate.setStatus("APPROVED");
        roommateRepository.save(roommate);
    }

    @Override
    public List<RoommateResponseDTO> getMyRoommates(Integer contractId) {
        checkContractOwnership(contractId);
        return roommateRepository.findByContractId(contractId).stream().map(r -> {
            RoommateResponseDTO dto = new RoommateResponseDTO();
            dto.setId(r.getId());
            dto.setFullName(r.getFullName());
            dto.setCccd(r.getCccd());
            dto.setPhone(r.getPhone());
            dto.setGender(r.getGender());
            dto.setHometown(r.getHometown());
            dto.setStatus(r.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteRoommate(Integer contractId, Integer roommateId) {
        checkContractOwnership(contractId);
        Roommate roommate = roommateRepository.findById(roommateId).orElseThrow();
        if (!roommate.getContract().getId().equals(contractId)) {
            throw new RuntimeException("Dữ liệu không đồng nhất!");
        }
        roommateRepository.delete(roommate);
    }

    private void checkContractOwnership(Integer contractId) {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        Contract contract = contractRepository.findById(contractId).orElseThrow();
        if (!contract.getUser().getPhone().equals(phone)) {
            throw new RuntimeException("Bạn không có quyền thao tác trên hợp đồng này!");
        }
    }

    private Double resolveDepositFromRoom(Room room) {
        if (room == null || room.getPrice() == null) return 0.0;
        return room.getPrice();
    }

    @Override
    public ContractResponseDTO getContractDetail(Integer id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));
        return mapToResponseDTO(contract);
    }
}