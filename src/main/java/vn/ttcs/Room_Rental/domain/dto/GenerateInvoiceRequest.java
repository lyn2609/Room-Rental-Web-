package vn.ttcs.Room_Rental.domain.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class GenerateInvoiceRequest {
    @NotBlank(message = "Thiếu tháng hóa đơn")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "Định dạng tháng không hợp lệ. Yêu cầu: yyyy-MM")
    private String month; // "2025-06"

    @NotNull(message = "Thiếu hạn thanh toán")
    private LocalDate dueDate;

    @NotEmpty(message = "Danh sách phòng phát hành hóa đơn không được rỗng")
    @Valid
    private List<RoomMeterReading> readings;

    public static class RoomMeterReading {
        @NotNull(message = "Thiếu contractId")
        @Positive(message = "contractId phải lớn hơn 0")
        private Integer contractId;

        @PositiveOrZero(message = "Chỉ số điện cũ phải >= 0")
        private Integer electricOldIndex;

        @PositiveOrZero(message = "Chỉ số điện mới phải >= 0")
        private Integer electricNewIndex;

        @PositiveOrZero(message = "Chỉ số nước cũ phải >= 0")
        private Integer waterOldIndex;

        @PositiveOrZero(message = "Chỉ số nước mới phải >= 0")
        private Integer waterNewIndex;

        public Integer getContractId() {
            return contractId;
        }

        public void setContractId(Integer contractId) {
            this.contractId = contractId;
        }

        public Integer getElectricOldIndex() {
            return electricOldIndex;
        }

        public void setElectricOldIndex(Integer v) {
            this.electricOldIndex = v;
        }

        public Integer getElectricNewIndex() {
            return electricNewIndex;
        }

        public void setElectricNewIndex(Integer v) {
            this.electricNewIndex = v;
        }

        public Integer getWaterOldIndex() {
            return waterOldIndex;
        }

        public void setWaterOldIndex(Integer v) {
            this.waterOldIndex = v;
        }

        public Integer getWaterNewIndex() {
            return waterNewIndex;
        }

        public void setWaterNewIndex(Integer v) {
            this.waterNewIndex = v;
        }
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public List<RoomMeterReading> getReadings() {
        return readings;
    }

    public void setReadings(List<RoomMeterReading> readings) {
        this.readings = readings;
    }
}
