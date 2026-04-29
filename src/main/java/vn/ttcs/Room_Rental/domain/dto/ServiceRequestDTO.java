package vn.ttcs.Room_Rental.domain.dto;

public class ServiceRequestDTO {
    private String name;
    private Double defaultPrice;
    private String unit;
    private Boolean isMetered;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getDefaultPrice() { return defaultPrice; }
    public void setDefaultPrice(Double defaultPrice) { this.defaultPrice = defaultPrice; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Boolean getIsMetered() { return isMetered; }
    public void setIsMetered(Boolean isMetered) { this.isMetered = isMetered; }
}