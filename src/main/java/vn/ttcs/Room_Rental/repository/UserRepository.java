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
}
