package vn.ttcs.Room_Rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ttcs.Room_Rental.domain.Roommate;
import java.util.List;

@Repository
public interface RoommateRepository extends JpaRepository<Roommate, Integer> {
    List<Roommate> findByContractId(Integer contractId);
}