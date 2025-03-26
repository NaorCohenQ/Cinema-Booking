package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.DTO.BookingRequest;
import com.att.tdp.popcorn_palace.DTO.MovieRequest;
import com.att.tdp.popcorn_palace.DTO.ShowtimeRequest;
import com.att.tdp.popcorn_palace.Models.Booking;
import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Models.Showtime;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScenarioTests {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void scenario1_deleteMovieAlsoDeletesShowtime() throws Exception {
        // Add a movie
        MovieRequest movieRequest = new MovieRequest("Titanic", "Drama", 120, "8.2", 1997);
        String movieJson = objectMapper.writeValueAsString(movieRequest);
        String movieId = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Movie savedMovie = objectMapper.readValue(movieId, Movie.class);

        // Add showtime
        ShowtimeRequest showtimeRequest = new ShowtimeRequest(
                savedMovie.getId(),
                "Room A",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                30.0
        );
        String showtimeJson = objectMapper.writeValueAsString(showtimeRequest);
        String showtimeId = mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(showtimeJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Showtime savedShowtime = objectMapper.readValue(showtimeId, Showtime.class);

        // Delete movie
        mockMvc.perform(delete("/movies/" + savedMovie.getTitle()))
                .andExpect(status().isOk());

        // Try to get deleted showtime
        mockMvc.perform(get("/showtimes/" + savedShowtime.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void scenario3_deleteShowtimeAlsoDeletesBooking() throws Exception {
        // Add a movie
        MovieRequest movieRequest = new MovieRequest("The Matrix", "Action", 130, "8.7", 1999);
        String movieJson = objectMapper.writeValueAsString(movieRequest);
        String movieId = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Movie savedMovie = objectMapper.readValue(movieId, Movie.class);

        // Add showtime
        ShowtimeRequest showtimeRequest = new ShowtimeRequest(
                savedMovie.getId(),
                "Room B",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                25.0
        );
        String showtimeJson = objectMapper.writeValueAsString(showtimeRequest);
        String showtimeId = mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(showtimeJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Showtime savedShowtime = objectMapper.readValue(showtimeId, Showtime.class);

        // Add booking
        BookingRequest bookingRequest = new BookingRequest(savedShowtime.getId(), 1, UUID.randomUUID());
        String bookingJson = objectMapper.writeValueAsString(bookingRequest);
        String bookingId = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Booking savedBooking = objectMapper.readValue(bookingId, Booking.class);

        // Delete showtime
        mockMvc.perform(delete("/showtimes/" + savedShowtime.getId()))
                .andExpect(status().isOk());

        // Try to get deleted booking
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString(savedBooking.getBookingId().toString()))));
    }
}
