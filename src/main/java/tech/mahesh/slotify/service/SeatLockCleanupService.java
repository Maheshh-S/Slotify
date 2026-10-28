package tech.mahesh.slotify.service;

import tech.mahesh.slotify.entity.Seat;
import tech.mahesh.slotify.enums.SeatStatus;
import tech.mahesh.slotify.repository.SeatRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeatLockCleanupService {

    private final SeatRepository seatRepository;

    public SeatLockCleanupService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    public void cleanupExpiredLocks() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
        List<Seat> expired = seatRepository.findExpiredLocks(threshold);
        for (Seat seat : expired) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLockedAt(null);
            seatRepository.save(seat);
        }
    }
}