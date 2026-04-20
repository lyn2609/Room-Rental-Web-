package vn.ttcs.Room_Rental.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.ttcs.Room_Rental.domain.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    // Client: lấy hóa đơn theo user (qua contract → user)
    @Query("SELECT i FROM Invoice i JOIN i.contract c WHERE c.user.id = :userId ORDER BY i.createdAt DESC")
    List<Invoice> findByUserId(@Param("userId") Integer userId);

    // Admin: filter theo tháng
    List<Invoice> findByMonth(String month);

    // Admin: filter theo tháng + contractId
    List<Invoice> findByMonthAndContract_Id(String month, Integer contractId);

    // Admin: filter theo contractId
    List<Invoice> findByContract_Id(Integer contractId);

    // Kiểm tra đã phát hành hóa đơn tháng này chưa
    Optional<Invoice> findByContract_IdAndMonth(Integer contractId, String month);

    // Dashboard: tổng hóa đơn theo tháng
    List<Invoice> findByMonthOrderByCreatedAtDesc(String month);

    List<Invoice> findByStatusNot(String status);

    // Dashboard: phòng đang nợ
    @Query("SELECT i FROM Invoice i WHERE i.status IN ('UNPAID', 'OVERDUE') ORDER BY i.dueDate ASC")
    List<Invoice> findAllDebtInvoices();

    @Query("SELECT i FROM Invoice i WHERE i.status IN ('UNPAID', 'OVERDUE') AND i.month = :month ORDER BY i.dueDate ASC")
    List<Invoice> findAllDebtInvoicesByMonth(@Param("month") String month);
}
