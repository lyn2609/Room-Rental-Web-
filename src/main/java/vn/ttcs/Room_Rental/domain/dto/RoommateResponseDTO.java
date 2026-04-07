package vn.ttcs.Room_Rental.domain.dto;

import lombok.Data;

@Data
public class RoommateResponseDTO {
    private Integer id;
    private String fullName;
    private String cccd;
    private String phone;
    private String gender;
    private String hometown;
    private String status;
}