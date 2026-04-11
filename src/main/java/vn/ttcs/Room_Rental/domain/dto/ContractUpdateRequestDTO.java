package vn.ttcs.Room_Rental.domain.dto;

import java.time.LocalDate;

public class ContractUpdateRequestDTO {
    // Chỉ cho phép cập nhật 2 trường này để đảm bảo an toàn logic
    private LocalDate endDate;
    private Double deposit;

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
}