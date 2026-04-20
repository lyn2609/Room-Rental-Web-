package vn.ttcs.Room_Rental.domain.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class UpdateInvoiceRequest {
    @NotNull(message = "Thiếu hạn thanh toán")
    private LocalDate dueDate;

    @PositiveOrZero(message = "Chỉ số điện cũ phải >= 0")
    private Integer electricOldIndex;

    @PositiveOrZero(message = "Chỉ số điện mới phải >= 0")
    private Integer electricNewIndex;

    @PositiveOrZero(message = "Chỉ số nước cũ phải >= 0")
    private Integer waterOldIndex;

    @PositiveOrZero(message = "Chỉ số nước mới phải >= 0")
    private Integer waterNewIndex;

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getElectricOldIndex() {
        return electricOldIndex;
    }

    public void setElectricOldIndex(Integer electricOldIndex) {
        this.electricOldIndex = electricOldIndex;
    }

    public Integer getElectricNewIndex() {
        return electricNewIndex;
    }

    public void setElectricNewIndex(Integer electricNewIndex) {
        this.electricNewIndex = electricNewIndex;
    }

    public Integer getWaterOldIndex() {
        return waterOldIndex;
    }

    public void setWaterOldIndex(Integer waterOldIndex) {
        this.waterOldIndex = waterOldIndex;
    }

    public Integer getWaterNewIndex() {
        return waterNewIndex;
    }

    public void setWaterNewIndex(Integer waterNewIndex) {
        this.waterNewIndex = waterNewIndex;
    }
}
