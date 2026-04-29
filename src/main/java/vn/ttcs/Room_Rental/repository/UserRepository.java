package vn.ttcs.Room_Rental.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.ttcs.Room_Rental.domain.User;

@Repository
public interface UserRepository extends JpaRepository <User, Integer>{
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByCccd(String cccd);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN u.contracts c " +
            "LEFT JOIN c.room r " +
            "WHERE u.role.name = 'CLIENT' " +
            "AND (:fullName IS NULL OR :fullName = '' OR u.fullName LIKE %:fullName%) " +
            "AND (:phone IS NULL OR :phone = '' OR u.phone LIKE %:phone%) " +
            "AND (:cccd IS NULL OR :cccd = '' OR u.cccd LIKE %:cccd%) " +
            "AND (:roomName IS NULL OR :roomName = '' OR r.name LIKE %:roomName%) " +
            "AND (:status IS NULL OR :status = '' OR u.status = :status)")
    java.util.List<vn.ttcs.Room_Rental.domain.User> searchClients(
            @org.springframework.data.repository.query.Param("fullName") String fullName,
            @org.springframework.data.repository.query.Param("phone") String phone,
            @org.springframework.data.repository.query.Param("cccd") String cccd,
            @org.springframework.data.repository.query.Param("roomName") String roomName,
            @org.springframework.data.repository.query.Param("status") String status);
}
