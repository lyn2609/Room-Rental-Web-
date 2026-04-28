package vn.ttcs.Room_Rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ttcs.Room_Rental.domain.BookingView;

@Repository
public interface BookingViewRepository extends JpaRepository<BookingView, Integer> {
}