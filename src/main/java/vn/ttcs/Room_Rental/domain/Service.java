package vn.ttcs.Room_Rental.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String unit;

    private Double defaultPrice;

    private Boolean isMetered = false;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    // --- MẢNH GHÉP MỚI: Nối sang bảng trung gian để quản lý giá chốt ---
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    private List<ContractServiceDetail> contractDetails;

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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(Double defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    public Boolean getIsMetered() {
        return isMetered;
    }

    public void setIsMetered(Boolean isMetered) {
        this.isMetered = isMetered;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    // --- GETTER/SETTER CHO CONTRACT DETAILS ---
    public List<ContractServiceDetail> getContractDetails() {
        return contractDetails;
    }

    public void setContractDetails(List<ContractServiceDetail> contractDetails) {
        this.contractDetails = contractDetails;
    }
}