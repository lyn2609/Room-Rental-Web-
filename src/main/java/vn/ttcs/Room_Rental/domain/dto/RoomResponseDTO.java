package vn.ttcs.Room_Rental.domain.dto;

import java.math.BigDecimal;

public class RoomResponseDTO {
    private Integer id;
    private String name;
    private String area; // Khu vực
    private String address;
    private Double price; // Giá thuê
    private String status; // Trạng thái (AVAILABLE, RENTED, MAINTENANCE)
    private String description;

    // Generate Getter/Setter (hoặc dùng Alt+Insert)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}