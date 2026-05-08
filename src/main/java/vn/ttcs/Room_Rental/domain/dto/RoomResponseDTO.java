package vn.ttcs.Room_Rental.domain.dto;

import java.util.List;

public class RoomResponseDTO {
    private Integer id;
    private String name;
    private String area;
    private String address;
    private Double price;
    private String status;
    private String imageUrl;
    private String shortDescription;
    private String detailedDescription;
    private Integer maxOccupants;
    private List<ServiceResponseDTO> services;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public Integer getMaxOccupants() {
        return maxOccupants;
    }

    public void setMaxOccupants(Integer maxOccupants) {
        this.maxOccupants = maxOccupants;
    }

    public List<ServiceResponseDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceResponseDTO> services) {
        this.services = services;
    }
}
