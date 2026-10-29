package tech.mahesh.slotify.controller;

import tech.mahesh.slotify.dto.*;
import tech.mahesh.slotify.service.BookingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/lock-seat")
    public ResponseEntity<BookingDto> lockSeat(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody LockSeatRequest request) {
        Long userId = Long.parseLong(user.getUsername());
        return ResponseEntity.ok(bookingService.lockSeat(userId, request.getSeatId()));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingDto> confirmBooking(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        Long userId = Long.parseLong(user.getUsername());
        return ResponseEntity.ok(bookingService.confirmBooking(userId, id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingDto> cancelBooking(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        Long userId = Long.parseLong(user.getUsername());
        return ResponseEntity.ok(bookingService.cancelBooking(userId, id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookingDto>> getBookings(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = Long.parseLong(user.getUsername());
        return ResponseEntity.ok(bookingService.getUserBookings(userId, page, size));
    }

    @Data
    @AllArgsConstructor
    static class LockSeatRequest {
        private Long seatId;
    }
}