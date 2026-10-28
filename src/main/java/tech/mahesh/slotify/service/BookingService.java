package tech.mahesh.slotify.service;

import tech.mahesh.slotify.dto.*;
import tech.mahesh.slotify.entity.*;
import tech.mahesh.slotify.enums.BookingStatus;
import tech.mahesh.slotify.enums.SeatStatus;
import tech.mahesh.slotify.exception.*;
import tech.mahesh.slotify.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BookingService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    public BookingService(SeatRepository seatRepository, BookingRepository bookingRepository, EventRepository eventRepository) {
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
    }

    public BookingDto lockSeat(Long userId, Long seatId) {
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> new SeatNotAvailableException("Seat not found"));

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new SeatNotAvailableException("Seat is not available");
        }

        Long version = seat.getVersion();
        int updated = seatRepository.lockSeat(seatId, version, LocalDateTime.now());

        if (updated == 0) {
            throw new SeatNotAvailableException("Seat was just booked by another user");
        }

        seat.setStatus(SeatStatus.LOCKED);
        seat.setLockedAt(LocalDateTime.now());
        seat.setVersion(version + 1);

        Booking booking = Booking.builder()
            .user(new User())
            .event(seat.getEvent())
            .seat(seat)
            .status(BookingStatus.PENDING)
            .build();
        booking.getUser().setId(userId);

        booking = bookingRepository.save(booking);
        return toDto(booking);
    }

    public BookingDto confirmBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
            .orElseThrow(() -> new BookingConflictException("Booking not found or access denied"));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return toDto(booking);
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingConflictException("Cannot confirm cancelled booking");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setConfirmedAt(LocalDateTime.now());
        booking.getSeat().setStatus(SeatStatus.BOOKED);

        return toDto(bookingRepository.save(booking));
    }

    public BookingDto cancelBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
            .orElseThrow(() -> new BookingConflictException("Booking not found or access denied"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return toDto(booking);
        }
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new BookingConflictException("Cannot cancel confirmed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.getSeat().setStatus(SeatStatus.AVAILABLE);
        booking.getSeat().setLockedAt(null);

        return toDto(bookingRepository.save(booking));
    }

    public PageResponse<BookingDto> getUserBookings(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 50), Sort.by("createdAt").descending());
        Page<Booking> bookings = bookingRepository.findByUserId(userId, pageable);
        return toPageResponse(bookings);
    }

    private BookingDto toDto(Booking b) {
        return BookingDto.builder()
            .id(b.getId())
            .eventId(b.getEvent().getId())
            .eventName(b.getEvent().getName())
            .seatId(b.getSeat().getId())
            .seatNumber(b.getSeat().getSeatNumber())
            .status(b.getStatus().name())
            .createdAt(b.getCreatedAt())
            .confirmedAt(b.getConfirmedAt())
            .cancelledAt(b.getCancelledAt())
            .build();
    }

    private PageResponse<BookingDto> toPageResponse(Page<Booking> page) {
        return PageResponse.<BookingDto>builder()
            .content(page.getContent().stream().map(this::toDto).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .build();
    }
}