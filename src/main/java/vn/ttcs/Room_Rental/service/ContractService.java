package vn.ttcs.Room_Rental.service;

import vn.ttcs.Room_Rental.domain.dto.*;
import java.util.List;

public interface ContractService {
    // 1. Quản lý Hợp đồng chính
    void createContract(ContractCreateRequestDTO requestDTO);

    /** Lấy danh sách hợp đồng cho Admin kèm bộ lọc (Status, Room, Client) */
    List<ContractResponseDTO> getAllContracts(String status, Integer roomId, String clientName);

    /** Chi tiết hợp đồng bao gồm cả thông tin phòng và dịch vụ chốt tại thời điểm ký */
    ContractResponseDTO getContractDetail(Integer id);

    /** Lấy tất cả hợp đồng mà User hiện tại đang đứng tên thuê */
    List<ContractResponseDTO> getMyContracts();

    // 2. Cập nhật & Lịch sử biến động
    /** Cập nhật thông tin: Trong Implementation sẽ tự động ghi log vào bảng Appendix */
    void updateContract(Integer id, ContractUpdateRequestDTO dto);

    void changeContractStatus(Integer id, ContractStatusRequestDTO dto);

    /** Lấy danh sách các lần thay đổi (Phụ lục) của một hợp đồng cụ thể */
    List<ContractAppendixResponseDTO> getContractAppendices(Integer contractId);

    void updateServicePrice(Integer serviceId, Double newPrice);

    // 3. Quản lý người ở ghép (Roommates)
    /** Thêm người ở ghép - DÀNH CHO CLIENT: Có kiểm tra token để xác định quyền chủ hợp đồng */
    void addRoommate(Integer contractId, RoommateRequestDTO dto);

    /** Thêm người ở ghép - DÀNH CHO ADMIN: Bỏ qua kiểm tra quyền sở hữu */
    void addRoommateByAdmin(Integer contractId, RoommateRequestDTO dto);

    void approveRoommate(Integer contractId, Integer roommateId);

    List<RoommateResponseDTO> getMyRoommates(Integer contractId);

    void deleteRoommate(Integer contractId, Integer roommateId);
}