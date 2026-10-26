package tech.mahesh.slotify.repository;

import tech.mahesh.slotify.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(attributePaths = {"seat", "seat.event", "event", "user"})
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"seat", "seat.event", "event", "user"})
    Optional<Booking> findByIdAndUserId(Long id, Long userId);

    List<Booking> findBySeatId(Long seatId);
}