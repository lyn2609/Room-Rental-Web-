package vn.ttcs.Room_Rental.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ContractCreateRequestDTO {
    @NotNull(message = "ID phòng không được để trống")
    private Integer roomId;

    @NotNull(message = "ID người thuê không được để trống")
    private Integer userId;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    @NotNull(message = "Tiền cọc không được để trống")
    private Double deposit;
}