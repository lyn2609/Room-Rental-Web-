package vn.ttcs.Room_Rental.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class InvoiceResponse {
    private Integer id;
    private String month;
    private String roomNumber;
    private String tenantName;
    private Double totalAmount;
    private LocalDate dueDate;
    private String status;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private List<InvoiceDetailResponse> details;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String v) {
        this.month = v;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String v) {
        this.roomNumber = v;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String v) {
        this.tenantName = v;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double v) {
        this.totalAmount = v;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate v) {
        this.dueDate = v;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String v) {
        this.status = v;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String v) {
        this.paymentMethod = v;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime v) {
        this.createdAt = v;
    }

    public List<InvoiceDetailResponse> getDetails() {
        return details;
    }

    public void setDetails(List<InvoiceDetailResponse> v) {
        this.details = v;
    }
}
