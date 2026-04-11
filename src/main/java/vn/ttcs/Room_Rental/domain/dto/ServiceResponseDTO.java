package vn.ttcs.Room_Rental.domain.dto;

public class ServiceResponseDTO {
    private String name;
    private Double price;
    private String unit; // ví dụ: kWh, m3, tháng

    public ServiceResponseDTO() {
    }

    public ServiceResponseDTO(String name, Double price, String unit) {
        this.name = name;
        this.price = price;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


}