package tech.mahesh.slotify.controller;

import tech.mahesh.slotify.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired private TestRestTemplate restTemplate;

    @Test
    void register_thenLogin_returnsToken() {
        RegisterRequest req = new RegisterRequest("test@test.com", "password123");
        ResponseEntity<AuthResponse> reg = restTemplate.postForEntity("/auth/register", req, AuthResponse.class);
        assertEquals(HttpStatus.OK, reg.getStatusCode());

        AuthRequest login = new AuthRequest("test@test.com", "password123");
        ResponseEntity<AuthResponse> lg = restTemplate.postForEntity("/auth/login", login, AuthResponse.class);
        assertEquals(HttpStatus.OK, lg.getStatusCode());
        assertNotNull(lg.getBody().getToken());
    }

    @Test
    void register_duplicateEmail_returnsError() {
        RegisterRequest req = new RegisterRequest("dup@test.com", "password");
        restTemplate.postForEntity("/auth/register", req, AuthResponse.class);
        ResponseEntity<AuthResponse> reg = restTemplate.postForEntity("/auth/register", req, AuthResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, reg.getStatusCode());
    }
}