package vn.ttcs.Room_Rental.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ttcs.Room_Rental.domain.Contract;
import vn.ttcs.Room_Rental.domain.Room;
import vn.ttcs.Room_Rental.domain.Roommate;
import vn.ttcs.Room_Rental.domain.User;
import vn.ttcs.Room_Rental.domain.dto.*;
import vn.ttcs.Room_Rental.repository.ContractRepository;
import vn.ttcs.Room_Rental.repository.RoomRepository;
import vn.ttcs.Room_Rental.repository.RoommateRepository;
import vn.ttcs.Room_Rental.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
// @RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoommateRepository roommateRepository;

    public ContractServiceImpl(ContractRepository contractRepository, RoomRepository roomRepository, UserRepository userRepository, RoommateRepository roommateRepository) {
        this.contractRepository = contractRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roommateRepository = roommateRepository;
    }


    @Override
    @Transactional
    public void createContract(ContractCreateRequestDTO dto) {
        // 1. Tìm phòng
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));

        // Ép kiểu an toàn bằng cách dùng .name() so sánh với chuỗi
        if (!"AVAILABLE".equals(room.getStatus())) {
            throw new RuntimeException("Phòng này hiện không trống để cho thuê!");
        }

        // 2. Tìm người dùng
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // 3. Tạo hợp đồng
        Contract contract = new Contract();
        contract.setUser(user);
        contract.setRoom(room);
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        contract.setDeposit(resolveDepositFromRoom(room));
        contract.setStatus("ACTIVE");
        room.setStatus("RENTED");

        // Chú ý: Thay ContractsStatusEnum bằng tên Enum chuẩn trong project của bạn
        // Ví dụ: contract.setStatus(ContractsStatusEnum.ACTIVE);

        // 4. Cập nhật trạng thái phòng
        // Chú ý: Thay RoomsStatusEnum bằng tên Enum chuẩn trong project của bạn
        // Ví dụ: room.setStatus(RoomsStatusEnum.RENTED);

        // 5. Lưu xuống Database
        roomRepository.save(room);
        contractRepository.save(contract);
    }

    @Override
    public List<ContractResponseDTO> getAllContracts(String status, Integer roomId, String clientName) {
        // 1. Lấy dữ liệu từ Repo với các bộ lọc
        List<Contract> contracts = contractRepository.findWithFilters(status, roomId, clientName);

        // 2. Map sang DTO
        return contracts.stream().map(c -> {
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

            // 3. XỬ LÝ DỊCH VỤ: Lôi từ Room ra để Admin xem cho tiện
            if (c.getRoom().getServices() != null) {
                List<ServiceResponseDTO> serviceDTOs = c.getRoom().getServices().stream()
                        .map(s -> new ServiceResponseDTO(
                                s.getName(),
                                s.getDefaultPrice(), // Lưu ý: Dùng đúng tên getDefaultPrice() trong Service.java của bạn
                                s.getUnit()
                        ))
                        .collect(Collectors.toList());
                dto.setServices(serviceDTOs);
            }

            return dto; // Trả về DTO đã đầy đủ thông tin
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateContract(Integer id, ContractUpdateRequestDTO dto) {
        // 1. Tìm hợp đồng xem có tồn tại không
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng số: " + id));

        // 2. Cập nhật dữ liệu mới (Nếu người dùng có truyền lên)
        if (dto.getEndDate() != null) {
            contract.setEndDate(dto.getEndDate());
        }
        contract.setDeposit(resolveDepositFromRoom(contract.getRoom()));

        // 3. Lưu lại
        contractRepository.save(contract);
    }

    @Override
    @Transactional
    public void changeContractStatus(Integer id, ContractStatusRequestDTO dto) {
        // 1. Tìm hợp đồng
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng số: " + id));

        String newStatus = dto.getStatus().toUpperCase(); // Viết hoa cho chuẩn Enum
        contract.setStatus(newStatus);

        // 2. Logic giải phóng/khóa phòng dựa trên trạng thái hợp đồng
        Room room = contract.getRoom();
        if ("TERMINATED".equals(newStatus) || "EXPIRED".equals(newStatus)) {
            // Chỉ trả phòng khi không còn hợp đồng ACTIVE nào khác của chính phòng đó
            boolean hasOtherActiveContract = contractRepository.existsByRoom_IdAndStatusAndIdNot(
                    room.getId(), "ACTIVE", contract.getId());
            room.setStatus(hasOtherActiveContract ? "RENTED" : "AVAILABLE");
        } else if ("ACTIVE".equals(newStatus)) {
            // Nếu kích hoạt lại -> Chuyển phòng thành ĐÃ THUÊ
            room.setStatus("RENTED");
        }

        // 3. Lưu thay đổi vào Database
        roomRepository.save(room);
        contractRepository.save(contract);
    }

    @Override
    public void addRoommate(Integer contractId, RoommateRequestDTO dto) {
        // 1. Xác định ai đang đăng nhập
        String identifier = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByPhone(identifier)
                .orElseThrow(() -> new RuntimeException("Lỗi xác thực người dùng"));

        // 2. Tìm hợp đồng và KIỂM TRA QUYỀN SỞ HỮU
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng số: " + contractId));

        if (!contract.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Lỗi bảo mật: Bạn không có quyền thao tác trên hợp đồng của người khác!");
        }

        // 3. Đổ dữ liệu từ DTO sang Entity Roommate
        Roommate roommate = new Roommate();
        roommate.setContract(contract);
        roommate.setFullName(dto.getFullName());
        roommate.setCccd(dto.getCccd());
        roommate.setPhone(dto.getPhone());
        roommate.setGender(dto.getGender());
        roommate.setHometown(dto.getHometown());
        roommate.setStatus("PENDING"); // Trạng thái mặc định là chờ duyệt

        // 4. Lưu vào Database
        roommateRepository.save(roommate);
    }

    @Override
    public void addRoommateByAdmin(Integer contractId, RoommateRequestDTO dto) {
        // Admin không cần kiểm tra quyền sở hữu - tìm hợp đồng trực tiếp
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng số: " + contractId));

        Roommate roommate = new Roommate();
        roommate.setContract(contract);
        roommate.setFullName(dto.getFullName());
        roommate.setCccd(dto.getCccd());
        roommate.setPhone(dto.getPhone());
        roommate.setGender(dto.getGender());
        roommate.setHometown(dto.getHometown());
        // Admin thêm thì duyệt thẳng luôn, không cần chờ
        roommate.setStatus("APPROVED");

        roommateRepository.save(roommate);
    }

    @Override
    public List<ContractResponseDTO> getMyContracts() {
        // 1. Lấy thông tin identifier (phone) từ Security Context
        String identifier = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Tìm User
        User user = userRepository.findByPhone(identifier)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hiện tại"));

        // 3. Tìm danh sách hợp đồng
        List<Contract> contracts = contractRepository.findByUserId(user.getId());

        // 4. Chuyển sang DTO
        return contracts.stream().map(contract -> {
            ContractResponseDTO dto = new ContractResponseDTO();
            dto.setId(contract.getId());
            dto.setRoomId(contract.getRoom().getId());
            dto.setRoomName(contract.getRoom().getName());
            dto.setUserId(contract.getUser().getId());
            dto.setUserFullName(contract.getUser().getFullName());
            dto.setStartDate(contract.getStartDate());
            dto.setEndDate(contract.getEndDate());
            dto.setDeposit(contract.getDeposit());
            dto.setStatus(contract.getStatus());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void approveRoommate(Integer contractId, Integer roommateId) {
        // 1. Tìm người ở ghép dựa trên ID và ID hợp đồng để đảm bảo chính xác
        Roommate roommate = roommateRepository.findById(roommateId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người ở ghép này"));

        // Kiểm tra xem người này có đúng thuộc hợp đồng đó không
        if (!roommate.getContract().getId().equals(contractId)) {
            throw new RuntimeException("Người ở ghép này không thuộc hợp đồng số " + contractId);
        }

        // 2. Cập nhật trạng thái thành APPROVED
        roommate.setStatus("APPROVED");

        // 3. Lưu lại vào Database
        roommateRepository.save(roommate);
    }

    @Override
    public List<RoommateResponseDTO> getMyRoommates(Integer contractId) {
        // 1. Kiểm tra quyền sở hữu hợp đồng (đã viết ở bước trước)
        checkContractOwnership(contractId);

        // 2. Lấy danh sách Entity
        List<Roommate> roommates = roommateRepository.findByContractId(contractId);

        // 3. Map sang DTO để ngắt vòng lặp
        return roommates.stream().map(r -> {
            RoommateResponseDTO dto = new RoommateResponseDTO();
            dto.setId(r.getId());
            dto.setFullName(r.getFullName());
            dto.setCccd(r.getCccd());
            dto.setPhone(r.getPhone());
            dto.setGender(r.getGender());
            dto.setHometown(r.getHometown());
            dto.setStatus(r.getStatus());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void deleteRoommate(Integer contractId, Integer roommateId) {
        // 1. Kiểm tra quyền sở hữu hợp đồng
        checkContractOwnership(contractId);

        // 2. Tìm người ở ghép
        Roommate roommate = roommateRepository.findById(roommateId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người ở ghép này"));

        // 3. Kiểm tra xem người này có đúng thuộc hợp đồng đó không
        if (!roommate.getContract().getId().equals(contractId)) {
            throw new RuntimeException("Dữ liệu không đồng nhất!");
        }

        // 4. Xóa khỏi DB
        roommateRepository.delete(roommate);
    }

    // Hàm dùng chung để kiểm tra quyền sở hữu hợp đồng cho gọn code
    private void checkContractOwnership(Integer contractId) {
        String phone = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

        if (!contract.getUser().getPhone().equals(phone)) {
            throw new RuntimeException("Bạn không có quyền thao tác trên hợp đồng này!");
        }
    }

    private Double resolveDepositFromRoom(Room room) {
        if (room == null || room.getPrice() == null) {
            return 0.0;
        }
        return room.getPrice();
    }

    @Override
    public ContractResponseDTO getContractDetail(Integer id) {
        // 1. Tìm hợp đồng
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng ID: " + id));

        // 2. Bảo mật: Chỉ chủ hợp đồng mới được xem
        String identifier = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        if (!contract.getUser().getPhone().equals(identifier)) {
            throw new RuntimeException("Bạn không có quyền xem chi tiết hợp đồng này!");
        }

        // 3. Map thông tin hợp đồng
        ContractResponseDTO dto = new ContractResponseDTO();
        dto.setId(contract.getId());
        dto.setRoomName(contract.getRoom().getName());
        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setDeposit(contract.getDeposit());
        dto.setStatus(contract.getStatus());

        // 4. Lấy danh sách dịch vụ từ Room (Giả sử Entity Room của bạn có List<Service> services)
        if (contract.getRoom().getServices() != null) {
            List<ServiceResponseDTO> serviceDTOs = contract.getRoom().getServices().stream()
                    .map(s -> new ServiceResponseDTO(s.getName(), s.getDefaultPrice(), s.getUnit()))
                    .collect(Collectors.toList());
            dto.setServices(serviceDTOs);
        }

        return dto;
    }
}