package tech.mahesh.slotify.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
    private Long id;

    @NotBlank @Size(max = 100)
    private String name;

    @NotBlank @Size(max = 200)
    private String location;

    @NotNull
    private java.time.LocalDateTime date;
}