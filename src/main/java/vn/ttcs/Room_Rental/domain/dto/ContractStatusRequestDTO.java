package vn.ttcs.Room_Rental.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContractStatusRequestDTO {
    @NotBlank(message = "Trạng thái không được để trống")
    private String status;
}