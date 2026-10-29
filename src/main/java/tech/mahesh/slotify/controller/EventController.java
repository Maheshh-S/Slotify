package tech.mahesh.slotify.controller;

import tech.mahesh.slotify.dto.*;
import tech.mahesh.slotify.entity.Event;
import tech.mahesh.slotify.entity.Seat;
import tech.mahesh.slotify.repository.EventRepository;
import tech.mahesh.slotify.repository.SeatRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    public EventController(EventRepository eventRepository, SeatRepository seatRepository) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
    }

    @GetMapping
    @Cacheable("events")
    public ResponseEntity<PageResponse<EventDto>> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 50));
        Page<Event> events = eventRepository.findAll(pageable);
        PageResponse<EventDto> response = PageResponse.<EventDto>builder()
            .content(events.getContent().stream().map(this::toDto).collect(Collectors.toList()))
            .page(events.getNumber())
            .size(events.getSize())
            .totalElements(events.getTotalElements())
            .totalPages(events.getTotalPages())
            .first(events.isFirst())
            .last(events.isLast())
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Cacheable(value = "event", key = "#id")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long id) {
        Event event = eventRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(toDto(event));
    }

    private EventDto toDto(Event e) {
        return EventDto.builder()
            .id(e.getId())
            .name(e.getName())
            .location(e.getLocation())
            .date(e.getDate())
            .build();
    }
}