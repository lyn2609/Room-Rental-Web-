package vn.ttcs.Room_Rental.domain.dto;

import java.time.LocalDate;
import java.util.List;

public class ContractResponseDTO {
    private Integer id;
    private Integer roomId;
    private String roomName;
    private Integer userId;
    private String userFullName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double deposit;
    private String status;
    private List<ServiceResponseDTO> services;

    // Getter và Setter cho id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // Getter và Setter cho roomId
    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    // Getter và Setter cho roomName
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    // Getter và Setter cho userId
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // Getter và Setter cho userFullName
    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    // Getter và Setter cho startDate
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // Getter và Setter cho endDate
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    // Getter và Setter cho deposit
    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    // Getter và Setter cho status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter và Setter cho danh sách dịch vụ kèm theo
    public List<ServiceResponseDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceResponseDTO> services) {
        this.services = services;
    }
}