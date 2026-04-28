package vn.ttcs.Room_Rental.domain.dto;

public class RoomRequestDTO {
    private String address;
    private String name;
    private String area;
    private Double price;
    private String status;
    private String description;

    // --- Getter và Setter ---
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}