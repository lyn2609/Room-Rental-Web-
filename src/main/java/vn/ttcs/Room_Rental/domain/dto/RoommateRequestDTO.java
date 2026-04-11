package vn.ttcs.Room_Rental.domain.dto;

public class RoommateRequestDTO {
    private String fullName;
    private String cccd;
    private String phone;
    private String gender;
    private String hometown;

    public RoommateRequestDTO() {
    }

    public RoommateRequestDTO(String fullName, String cccd, String phone, String gender, String hometown) {
        this.fullName = fullName;
        this.cccd = cccd;
        this.phone = phone;
        this.gender = gender;
        this.hometown = hometown;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }
}