package vn.ttcs.Room_Rental.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.ttcs.Room_Rental.domain.Contract;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {

    List<Contract> findByUserId(Integer userId); // Fix lỗi dòng 168

    @Query("SELECT c FROM Contract c WHERE " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:roomId IS NULL OR c.room.id = :roomId) AND " +
            "(:clientName IS NULL OR c.user.fullName LIKE %:clientName%)")
    List<Contract> findWithFilters(
            @Param("status") String status,
            @Param("roomId") Integer roomId,
            @Param("clientName") String clientName);
}