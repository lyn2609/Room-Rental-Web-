package vn.ttcs.Room_Rental.domain.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ContractUpdateRequestDTO {
    // Chỉ cho phép cập nhật 2 trường này để đảm bảo an toàn logic
    private LocalDate endDate;
    private Double deposit;
}