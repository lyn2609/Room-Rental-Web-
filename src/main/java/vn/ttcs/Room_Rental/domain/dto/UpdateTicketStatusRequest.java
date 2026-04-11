package vn.ttcs.Room_Rental.domain.dto;

import jakarta.validation.constraints.Pattern;

public class UpdateTicketStatusRequest {
    @Pattern(regexp = "PENDING|IN_PROGRESS|DONE",
             message = "status phải là PENDING, IN_PROGRESS hoặc DONE")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
