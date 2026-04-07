package vn.ttcs.Room_Rental.domain.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ContractResponseDTO {
    private Integer id;
    private Integer roomId;
    private String roomName;
    private Integer userId;
    private String userFullName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double deposit;
    private String status;
    private List<ServiceResponseDTO> services;
}
