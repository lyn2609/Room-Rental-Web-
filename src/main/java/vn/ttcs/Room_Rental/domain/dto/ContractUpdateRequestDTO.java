package vn.ttcs.Room_Rental.domain.dto;

import java.time.LocalDate;
import java.util.List; // Nhớ import thêm cái này nha ông giáo

public class ContractUpdateRequestDTO {
    // 1. Cập nhật thông tin cơ bản của hợp đồng
    private LocalDate endDate;
    private Double deposit;

    // 2. Danh sách ID các dịch vụ mà khách chọn (MỚI BỔ SUNG)
    // Dùng để so sánh và ghi log "Thêm/Bớt dịch vụ" vào Phụ lục
    private List<Integer> serviceIds;

    // 3. Lý do thay đổi (Dùng để ghi vào cột content của bảng contract_appendices)
    private String note;

    // --- GETTER & SETTER ---
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getDeposit() {
        return deposit;
    }
    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public List<Integer> getServiceIds() {
        return serviceIds;
    }
    public void setServiceIds(List<Integer> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
}