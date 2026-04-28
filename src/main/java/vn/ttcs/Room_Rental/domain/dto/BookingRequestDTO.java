package vn.ttcs.Room_Rental.domain.dto;

import java.time.LocalDateTime;

public class BookingRequestDTO {
    private LocalDateTime appointmentTime; // Ngày giờ hẹn xem
    private String note; // Ghi chú thêm

    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}