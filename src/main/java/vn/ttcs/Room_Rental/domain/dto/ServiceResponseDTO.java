package vn.ttcs.Room_Rental.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponseDTO {
    private String name;
    private Double price;
    private String unit; // ví dụ: kWh, m3, tháng
}