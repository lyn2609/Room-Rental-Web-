package vn.ttcs.Room_Rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.ttcs.Room_Rental.domain.Room;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    // Tìm kiếm phòng theo khu vực và khoảng giá (Chỉ lấy những phòng đang trống - AVAILABLE)
    @Query("SELECT r FROM Room r WHERE r.status = 'AVAILABLE' " +
            "AND (:area IS NULL OR :area = '' OR r.area LIKE %:area%) " +
            "AND (:minPrice IS NULL OR r.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR r.price <= :maxPrice)")
    List<Room> searchRooms(@Param("area") String area,
                           @Param("minPrice") Double minPrice,
                           @Param("maxPrice") Double maxPrice);
}