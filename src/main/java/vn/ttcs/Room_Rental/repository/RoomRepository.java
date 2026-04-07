package vn.ttcs.Room_Rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ttcs.Room_Rental.domain.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
}