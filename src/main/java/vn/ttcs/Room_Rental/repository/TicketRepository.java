package vn.ttcs.Room_Rental.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.ttcs.Room_Rental.domain.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    List<Ticket> findByContractIdOrderByCreatedAtDesc(Integer contractId);
    Optional<Ticket> findByIdAndContractId(Integer id, Integer contractId);
    @Query("SELECT t FROM Ticket t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) " +
           "ORDER BY t.createdAt DESC")
    Page<Ticket> findAllWithFilters(
            @Param("status") String status,
            @Param("priority") String priority,
            Pageable pageable);
}