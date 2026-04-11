package vn.ttcs.Room_Rental.domain.dto;

import java.time.LocalDateTime;

import vn.ttcs.Room_Rental.domain.Ticket;

public class TicketResponse {
    private Integer id;
    private String title;
    private String category;
    private String priority;
    private String status;
    private String description;
    private Integer contractId; 
    private String clientName; 
    private LocalDateTime createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static TicketResponse fromEntity(Ticket ticket) {
        TicketResponse res = new TicketResponse();
        res.id = ticket.getId();
        res.title = ticket.getTitle();
        res.category = ticket.getCategory();
        res.priority = ticket.getPriority();
        res.status = ticket.getStatus();
        res.description = ticket.getDescription();
        res.contractId = ticket.getContract() != null ? ticket.getContract().getId() : null;
        res.createdAt = ticket.getCreatedAt();
        if (ticket.getContract() != null && ticket.getContract().getUser() != null) {
            res.clientName = ticket.getContract().getUser().getFullName();
        }
        return res;
    }
}
