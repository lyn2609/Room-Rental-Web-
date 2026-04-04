package vn.ttcs.Room_Rental.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.ttcs.Room_Rental.domain.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer>{
    List<Vehicle> findByUserId(Integer userId);
    Optional<Vehicle> findByIdAndUserId(Integer id, Integer userId);
    boolean existsByLicensePlateAndUserId(String licensePlate, Integer userId);
}
