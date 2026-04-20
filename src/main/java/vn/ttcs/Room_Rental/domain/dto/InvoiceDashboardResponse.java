package vn.ttcs.Room_Rental.domain.dto;

import java.util.List;

public class InvoiceDashboardResponse {
    private Double totalRevenue;
    private Double collectedAmount;
    private Double pendingAmount;
    private Double collectionRate;
    private Integer totalInvoices;
    private Integer paidInvoices;
    private Integer unpaidInvoices;
    private Integer overdueInvoices;
    private List<DebtRoomInfo> debtRooms;

    public static class DebtRoomInfo {
        private String roomNumber;
        private String tenantName;
        private Double amount;
        private String month;
        private String status;

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

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double v) {
            this.amount = v;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String v) {
            this.month = v;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String v) {
            this.status = v;
        }
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double v) {
        this.totalRevenue = v;
    }

    public Double getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(Double v) {
        this.collectedAmount = v;
    }

    public Double getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(Double v) {
        this.pendingAmount = v;
    }

    public Double getCollectionRate() {
        return collectionRate;
    }

    public void setCollectionRate(Double v) {
        this.collectionRate = v;
    }

    public Integer getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(Integer v) {
        this.totalInvoices = v;
    }

    public Integer getPaidInvoices() {
        return paidInvoices;
    }

    public void setPaidInvoices(Integer v) {
        this.paidInvoices = v;
    }

    public Integer getUnpaidInvoices() {
        return unpaidInvoices;
    }

    public void setUnpaidInvoices(Integer v) {
        this.unpaidInvoices = v;
    }

    public Integer getOverdueInvoices() {
        return overdueInvoices;
    }

    public void setOverdueInvoices(Integer v) {
        this.overdueInvoices = v;
    }

    public List<DebtRoomInfo> getDebtRooms() {
        return debtRooms;
    }

    public void setDebtRooms(List<DebtRoomInfo> v) {
        this.debtRooms = v;
    }
}
