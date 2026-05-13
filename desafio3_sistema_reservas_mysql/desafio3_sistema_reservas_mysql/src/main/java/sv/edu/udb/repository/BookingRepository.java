package sv.edu.udb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sv.edu.udb.model.Booking;
import sv.edu.udb.model.BookingStatus;
import sv.edu.udb.model.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUser(User user);

    @Query("select coalesce(sum(b.quantity), 0) from Booking b where b.event.idEvent = :eventId and b.status = :status")
    Integer sumConfirmedQuantityByEventId(@Param("eventId") Integer eventId, @Param("status") BookingStatus status);
}
