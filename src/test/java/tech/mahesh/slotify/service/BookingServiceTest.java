package tech.mahesh.slotify.service;

import tech.mahesh.slotify.dto.BookingDto;
import tech.mahesh.slotify.entity.*;
import tech.mahesh.slotify.enums.*;
import tech.mahesh.slotify.exception.SeatNotAvailableException;
import tech.mahesh.slotify.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceTest {

    @Autowired private BookingService bookingService;
    @Autowired private SeatRepository seatRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;

    private Long eventId;
    private Long seatId;
    private Long userId;

    @BeforeEach
    void setup() {
        User user = User.builder().email("test@test.com").password("pass").role(UserRole.USER).build();
        userId = userRepository.save(user).getId();

        Event event = Event.builder().name("Test").location("Test").date(java.time.LocalDateTime.now().plusDays(1)).build();
        eventId = eventRepository.save(event).getId();

        Seat seat = Seat.builder().event(event).seatNumber("A01").status(SeatStatus.AVAILABLE).build();
        seatId = seatRepository.save(seat).getId();
    }

    @Test
    void lockSeat_whenAvailable_returnsBooking() {
        BookingDto dto = bookingService.lockSeat(userId, seatId);
        assertNotNull(dto.getId());
        assertEquals("PENDING", dto.getStatus());
    }

    @Test
    void lockSeat_whenAlreadyLocked_throwsException() {
        bookingService.lockSeat(userId, seatId);
        assertThrows(SeatNotAvailableException.class, () -> bookingService.lockSeat(userId, seatId));
    }

    @Test
    void concurrentLockSeat_onlyOneSucceeds() throws InterruptedException {
        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try { bookingService.lockSeat(userId, seatId); }
                catch (Exception ignored) {}
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();

        long locked = seatRepository.findAll().stream()
            .filter(s -> s.getStatus() == SeatStatus.LOCKED).count();
        assertEquals(1, locked);
    }

    @Test
    void confirmBooking_idempotent() {
        BookingDto dto = bookingService.lockSeat(userId, seatId);
        BookingDto confirmed = bookingService.confirmBooking(userId, dto.getId());
        assertEquals("CONFIRMED", confirmed.getStatus());
        BookingDto again = bookingService.confirmBooking(userId, dto.getId());
        assertEquals(confirmed.getId(), again.getId());
    }

    @Test
    void cancelBooking_idempotent() {
        BookingDto dto = bookingService.lockSeat(userId, seatId);
        BookingDto cancelled = bookingService.cancelBooking(userId, dto.getId());
        assertEquals("CANCELLED", cancelled.getStatus());
        BookingDto again = bookingService.cancelBooking(userId, dto.getId());
        assertEquals(cancelled.getId(), again.getId());
    }
}