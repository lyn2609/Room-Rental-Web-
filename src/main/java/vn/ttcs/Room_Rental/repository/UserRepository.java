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
            "WHERE u.role.name = 'CLIENT' " + // <--- CHỈ THÊM ".name" Ở ĐÂY LÀ HẾT LỖI
            "AND (:keyword IS NULL OR :keyword = '' " +
            "OR u.fullName LIKE %:keyword% " +
            "OR u.cccd LIKE %:keyword% " +
            "OR r.name LIKE %:keyword%)")
    java.util.List<vn.ttcs.Room_Rental.domain.User> searchClients(@org.springframework.data.repository.query.Param("keyword") String keyword);
}
