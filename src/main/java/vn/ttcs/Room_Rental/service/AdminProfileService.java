package vn.ttcs.Room_Rental.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.ttcs.Room_Rental.domain.Vehicle;
import vn.ttcs.Room_Rental.domain.dto.AdminVehicleResponse;
import vn.ttcs.Room_Rental.repository.VehicleRepository;

@Service
public class AdminProfileService {

    private final VehicleRepository vehicleRepository;

    public AdminProfileService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<AdminVehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::toAdminVehicleResponse)
                .collect(Collectors.toList());
    }

    public AdminVehicleResponse updateVehicleStatus(Integer vehicleId, String status) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));
        
        vehicle.setStatus(status.toUpperCase());
        return toAdminVehicleResponse(vehicleRepository.save(vehicle));
    }

    private AdminVehicleResponse toAdminVehicleResponse(Vehicle v) {
        AdminVehicleResponse res = new AdminVehicleResponse();
        res.setId(v.getId());
        res.setLicensePlate(v.getLicensePlate());
        res.setVehicleType(v.getVehicleType());
        res.setBrand(v.getBrand());
        res.setModel(v.getModel());
        res.setNote(v.getNote());
        res.setStatus(v.getStatus());
        res.setCreatedAt(v.getCreatedAt());

        if (v.getUser() != null) {
            res.setUserId(v.getUser().getId());
            res.setUserFullName(v.getUser().getFullName());
            res.setUserPhone(v.getUser().getPhone());
        }

        return res;
    }
}
