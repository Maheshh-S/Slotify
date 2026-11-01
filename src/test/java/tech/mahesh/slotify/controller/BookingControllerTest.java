package tech.mahesh.slotify.controller;

import tech.mahesh.slotify.dto.*;
import tech.mahesh.slotify.entity.*;
import tech.mahesh.slotify.enums.*;
import tech.mahesh.slotify.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingControllerTest {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private EventRepository eventRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private UserRepository userRepository;

    private String token;
    private Long eventId;
    private Long seatId;

    @BeforeEach
    void setup() {
        User user = User.builder().email("book@test.com").password("password").role(UserRole.USER).build();
        userRepository.save(user);
        AuthRequest login = new AuthRequest("book@test.com", "password");
        ResponseEntity<AuthResponse> lg = restTemplate.postForEntity("/auth/login", login, AuthResponse.class);
        token = lg.getBody().getToken();

        Event event = Event.builder().name("Test Event").location("Test").date(java.time.LocalDateTime.now().plusDays(1)).build();
        eventId = eventRepository.save(event).getId();
        Seat seat = Seat.builder().event(event).seatNumber("A01").status(SeatStatus.AVAILABLE).build();
        seatId = seatRepository.save(seat).getId();
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    void lockSeat_returnsBooking() {
        HttpEntity<BookingController.LockSeatRequest> req = new HttpEntity<>(new BookingController.LockSeatRequest(seatId), authHeaders());
        ResponseEntity<BookingDto> res = restTemplate.exchange("/bookings/lock-seat", HttpMethod.POST, req, BookingDto.class);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("PENDING", res.getBody().getStatus());
    }

    @Test
    void confirmBooking_works() {
        HttpEntity<BookingController.LockSeatRequest> lockReq = new HttpEntity<>(new BookingController.LockSeatRequest(seatId), authHeaders());
        ResponseEntity<BookingDto> lockRes = restTemplate.exchange("/bookings/lock-seat", HttpMethod.POST, lockReq, BookingDto.class);
        Long bookingId = lockRes.getBody().getId();

        HttpEntity<Void> confirmReq = new HttpEntity<>(authHeaders());
        ResponseEntity<BookingDto> confirmRes = restTemplate.exchange("/bookings/" + bookingId + "/confirm", HttpMethod.POST, confirmReq, BookingDto.class);
        assertEquals(HttpStatus.OK, confirmRes.getStatusCode());
        assertEquals("CONFIRMED", confirmRes.getBody().getStatus());
    }

    @Test
    void getBookings_returnsPage() {
        HttpEntity<Void> req = new HttpEntity<>(authHeaders());
        ResponseEntity<PageResponse<BookingDto>> res = restTemplate.exchange("/bookings?page=0&size=10", HttpMethod.GET, req, 
            new org.springframework.core.ParameterizedTypeReference<PageResponse<BookingDto>>() {});
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }
}