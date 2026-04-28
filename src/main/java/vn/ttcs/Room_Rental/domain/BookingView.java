package vn.ttcs.Room_Rental.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "booking_views")
public class BookingView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Khách đặt lịch

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room; // Phòng muốn xem

    @Column(nullable = false)
    private LocalDateTime appointmentTime; // Giờ hẹn

    @Column(columnDefinition = "TEXT")
    private String note; // Ghi chú của khách

    private String status = "PENDING"; // PENDING, APPROVED, CANCELLED

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}