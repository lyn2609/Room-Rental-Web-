package vn.ttcs.Room_Rental.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoommateRequestDTO {
    private String fullName;
    private String cccd;
    private String phone;
    private String gender;
    private String hometown;
}