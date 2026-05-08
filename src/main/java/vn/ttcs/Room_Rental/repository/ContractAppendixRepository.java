package vn.ttcs.Room_Rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ttcs.Room_Rental.domain.ContractAppendix;
import java.util.List;

@Repository
public interface ContractAppendixRepository extends JpaRepository<ContractAppendix, Integer> {
    List<ContractAppendix> findByContractIdOrderByCreatedAtDesc(Integer contractId);
}