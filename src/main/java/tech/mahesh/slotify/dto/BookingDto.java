package tech.mahesh.slotify.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private Long eventId;
    private String eventName;
    private Long seatId;
    private String seatNumber;
    private String status;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime confirmedAt;
    private java.time.LocalDateTime cancelledAt;
}