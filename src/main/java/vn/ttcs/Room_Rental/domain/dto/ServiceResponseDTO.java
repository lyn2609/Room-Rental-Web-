package vn.ttcs.Room_Rental.domain.dto;

public class ServiceResponseDTO {
    private Integer id;
    private String name;
    private Double price;
    private String unit;
    private Boolean isMetered;

    public ServiceResponseDTO() {}

    public ServiceResponseDTO(Integer id, String name, Double price, String unit, Boolean isMetered) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.isMetered = isMetered;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Boolean getIsMetered() { return isMetered; }
    public void setIsMetered(Boolean isMetered) { this.isMetered = isMetered; }
}