package vn.ttcs.Room_Rental.service;

import vn.ttcs.Room_Rental.domain.dto.*;
import java.util.List;

public interface ContractService {
    // 1. Tạo hợp đồng
    void createContract(ContractCreateRequestDTO requestDTO);

    // 2. Lấy danh sách hợp đồng cho Admin (Dùng 1 hàm này thôi để Filter luôn)
    List<ContractResponseDTO> getAllContracts(String status, Integer roomId, String clientName);

    // 3. Chi tiết hợp đồng + Dịch vụ kèm theo (Phần vừa mới xong nè)
    ContractResponseDTO getContractDetail(Integer id);

    // 4. Lấy hợp đồng của tôi (Dành cho Client)
    List<ContractResponseDTO> getMyContracts();

    // 5. Cập nhật và thay đổi trạng thái
    void updateContract(Integer id, ContractUpdateRequestDTO dto);
    void changeContractStatus(Integer id, ContractStatusRequestDTO dto);

    // 6. Quản lý người ở ghép (Roommates)
    void addRoommate(Integer contractId, RoommateRequestDTO dto);
    void approveRoommate(Integer contractId, Integer roommateId);
    List<RoommateResponseDTO> getMyRoommates(Integer contractId);
    void deleteRoommate(Integer contractId, Integer roommateId);
}