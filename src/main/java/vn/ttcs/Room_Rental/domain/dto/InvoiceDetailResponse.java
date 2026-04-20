package vn.ttcs.Room_Rental.domain.dto;

public class InvoiceDetailResponse {
    private Integer id;
    private String description; // Nhãn dòng (dùng cho Tiền phòng hoặc Service)
    private String serviceName;
    private String serviceUnit;
    private Double unitPrice;
    private Integer oldIndex;
    private Integer newIndex;
    private Integer quantity;
    private Double subtotal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String v) {
        this.serviceName = v;
    }

    public String getServiceUnit() {
        return serviceUnit;
    }

    public void setServiceUnit(String v) {
        this.serviceUnit = v;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double v) {
        this.unitPrice = v;
    }

    public Integer getOldIndex() {
        return oldIndex;
    }

    public void setOldIndex(Integer v) {
        this.oldIndex = v;
    }

    public Integer getNewIndex() {
        return newIndex;
    }

    public void setNewIndex(Integer v) {
        this.newIndex = v;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer v) {
        this.quantity = v;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double v) {
        this.subtotal = v;
    }
}
