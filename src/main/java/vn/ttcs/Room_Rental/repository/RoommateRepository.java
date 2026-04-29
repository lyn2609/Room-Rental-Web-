package vn.ttcs.Room_Rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ttcs.Room_Rental.domain.Roommate;
import java.util.List;

@Repository
public interface RoommateRepository extends JpaRepository<Roommate, Integer> {

    // Tìm bạn cùng phòng theo ID hợp đồng (Cái cũ của bạn)
    List<Roommate> findByContractId(Integer contractId);

    // MỚI: Tìm tất cả yêu cầu theo trạng thái (Ví dụ: "PENDING")
    List<Roommate> findByStatus(String status);
}