package vn.ttcs.Room_Rental.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.ttcs.Room_Rental.domain.InvoiceDetail;

public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Integer> {
    List<InvoiceDetail> findByInvoice_Id(Integer invoiceId);

    List<InvoiceDetail> findByInvoice_IdIn(List<Integer> invoiceIds);

    void deleteByInvoice_Id(Integer invoiceId);
}