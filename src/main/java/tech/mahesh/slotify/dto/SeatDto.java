package tech.mahesh.slotify.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatDto {
    private Long id;
    private Long eventId;
    private String seatNumber;
    private String status;
}