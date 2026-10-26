package tech.mahesh.slotify.repository;

import tech.mahesh.slotify.entity.Seat;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @EntityGraph(attributePaths = {"event"})
    List<Seat> findByEventId(Long eventId);

    @Modifying
    @Query("UPDATE Seat s SET s.status = 'LOCKED', s.lockedAt = :now WHERE s.id = :id AND s.version = :version AND s.status = 'AVAILABLE'")
    int lockSeat(@Param("id") Long id, @Param("version") Long version, @Param("now") LocalDateTime now);

    @Query("SELECT s FROM Seat s WHERE s.status = 'LOCKED' AND s.lockedAt < :threshold")
    List<Seat> findExpiredLocks(@Param("threshold") LocalDateTime threshold);
}