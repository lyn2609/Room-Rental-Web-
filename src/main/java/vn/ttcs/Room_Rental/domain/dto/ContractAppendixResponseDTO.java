package vn.ttcs.Room_Rental.domain.dto;

import java.time.LocalDateTime;

public class ContractAppendixResponseDTO {
    private Integer id;
    private String content;
    private LocalDateTime createdAt;

    // Constructor không tham số
    public ContractAppendixResponseDTO() {
    }

    // Constructor có tham số để dùng nhanh trong Stream/Map
    public ContractAppendixResponseDTO(Integer id, String content, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}