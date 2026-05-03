package vn.ttcs.Room_Rental.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClientResponseDTO {
    private Integer id;
    private String fullName;
    private String phone;
    private String email;
    private String cccd;
    private String address;
    private LocalDate dob; // Ngày sinh
    private String gender;
    private String avatarUrl;
    private String status;
    private String roomName; // Thông tin từ bảng Room (giữ lại từ code cũ của ông)

    // --- Thông tin ngân hàng để thanh toán ---
    private String bankName;
    private String bankAccountName;
    private String bankAccountNumber;

    private LocalDateTime createdAt;

    // --- Constructor ---
    public ClientResponseDTO() {}

    // --- Getter và Setter ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankAccountName() { return bankAccountName; }
    public void setBankAccountName(String bankAccountName) { this.bankAccountName = bankAccountName; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}