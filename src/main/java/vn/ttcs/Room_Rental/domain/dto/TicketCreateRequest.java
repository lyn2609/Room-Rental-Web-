package vn.ttcs.Room_Rental.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class TicketCreateRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @Pattern(regexp = "MAINTENANCE|TECHNICAL|COMPLAINT",
             message = "category phải là MAINTENANCE, TECHNICAL hoặc COMPLAINT")
    private String category = "MAINTENANCE";
    @Pattern(regexp = "URGENT|NORMAL",
             message = "priority phải là URGENT hoặc NORMAL")
    private String priority = "NORMAL";

    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
