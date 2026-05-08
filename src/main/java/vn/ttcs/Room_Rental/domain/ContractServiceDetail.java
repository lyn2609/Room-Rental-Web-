package vn.ttcs.Room_Rental.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "contract_services")
public class ContractServiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(name = "actual_price")
    private Double actualPrice; // Đây chính là "giá chốt" lúc ký hợp đồng

    // Getter và Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Contract getContract() { return contract; }
    public void setContract(Contract contract) { this.contract = contract; }
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
    public Double getActualPrice() { return actualPrice; }
    public void setActualPrice(Double actualPrice) { this.actualPrice = actualPrice; }
}