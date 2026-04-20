package vn.ttcs.Room_Rental.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.ttcs.Room_Rental.domain.Service;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
    List<Service> findByRoom_Id(Integer roomId);
}
