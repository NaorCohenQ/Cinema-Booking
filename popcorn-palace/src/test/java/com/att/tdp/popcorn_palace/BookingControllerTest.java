package com.att.tdp.popcorn_palace;
import com.att.tdp.popcorn_palace.DTO.BookingRequest;
import com.att.tdp.popcorn_palace.Models.Booking;
import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Models.Showtime;
import com.att.tdp.popcorn_palace.Repositories.BookingRepository;
import com.att.tdp.popcorn_palace.Repositories.MovieRepository;
import com.att.tdp.popcorn_palace.Repositories.ShowtimeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Movie movie;
    private Showtime showtime;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        showtimeRepository.deleteAll();
        movieRepository.deleteAll();

        movie = movieRepository.save(new Movie("Oppenheimer", "Drama", 180, "9.0", 2023));
        showtime = showtimeRepository.save(new Showtime(
                movie.getId(),
                "Hall A",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(4),
                50.0
        ));
    }

    @Test
    void testCreateBooking_Success() throws Exception {
        BookingRequest request = new BookingRequest(
                showtime.getId(), 1, UUID.randomUUID()
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").exists());
    }

    @Test
    void testCreateBooking_SeatAlreadyBooked() throws Exception {
        UUID userId1 = UUID.randomUUID();
        BookingRequest request1 = new BookingRequest(showtime.getId(), 10, userId1);
        bookingRepository.save(new Booking(request1));

        BookingRequest request2 = new BookingRequest(showtime.getId(), 10, UUID.randomUUID());

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Seat 10 is already booked")));
    }

    @Test
    void testCreateBooking_InvalidShowtime() throws Exception {
        BookingRequest request = new BookingRequest(
                99999L, 1, UUID.randomUUID()
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")));
    }

    @Test
    void testCreateBooking_InvalidRequestBody() throws Exception {
        String invalidJson = """
            {
              "seatNumber": 5
            }
        """;

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateBooking_InvalidUUIDFormat_Returns400() throws Exception {
        Movie movie = movieRepository.save(new Movie("Matrix", "Action", 136, "8.7", 1999));
        Showtime showtime = showtimeRepository.save(new Showtime(
                movie.getId(), "Main Theater",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3),
                45.0
        ));

        String invalidJson = """
        {
            "showtimeId": %d,
            "seatNumber": 7,
            "userId": "invalid-uuid-format"
        }
    """.formatted(showtime.getId());

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid")));
    }

}


