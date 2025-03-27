package com.att.tdp.popcorn_palace.ControllerTests;

import com.att.tdp.popcorn_palace.Conflicts.ErrorMessages;
import com.att.tdp.popcorn_palace.DTO.BookingRequest;
import com.att.tdp.popcorn_palace.DTO.MovieRequest;
import com.att.tdp.popcorn_palace.DTO.ShowtimeRequest;
import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Models.Showtime;
import com.att.tdp.popcorn_palace.Repositories.BookingRepository;
import com.att.tdp.popcorn_palace.Repositories.MovieRepository;
import com.att.tdp.popcorn_palace.Repositories.ShowtimeRepository;
import com.att.tdp.popcorn_palace.Services.MovieServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.time.LocalDateTime;
import java.util.UUID;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScenarioTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void cleanUp() {
        bookingRepository.deleteAll();
        showtimeRepository.deleteAll();
        movieRepository.deleteAll();
    }
    @Test
    void testMovieLifecycleScenario() throws Exception {
        // Step 1: Add a movie
        MovieRequest movie = new MovieRequest("The Meg", "Action", 113, 6.0, 2018);
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk());

        // Step 2: Update the movie
        MovieRequest updated = new MovieRequest("The Meg", "Thriller", 113, 6.5, 2018);
        mockMvc.perform(post("/movies/update/The Meg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(6.5))
                .andExpect(jsonPath("$.genre").value("Thriller"));

        // Step 3: Try adding same movie again
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isConflict());

        // Step 4: Delete the movie
        mockMvc.perform(delete("/movies/The Meg"))
                .andExpect(status().isOk());

        // Step 5: Confirm it's gone
        mockMvc.perform(get("/movies/The Meg"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddMultipleShowtimesForSameMovieSameTheater() throws Exception {
        // Step 1: Add Movie M1
        MovieRequest movie = new MovieRequest("M1", "Action", 100, 7.2, 2020);
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk());

        // Get Movie ID
        Movie m1 = movieRepository.findByTitle("M1").orElseThrow();

        // Step 2: Add 1st Showtime in Theater 1
        ShowtimeRequest s1 = new ShowtimeRequest(
                m1.getId(),
                "Theater 1",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusMinutes(100),
                30.0
        );
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(s1)))
                .andExpect(status().isOk());

        // Step 3: Add another showtime (later that day in same theater)
        ShowtimeRequest s2 = new ShowtimeRequest(
                m1.getId(),
                "Theater 1",
                LocalDateTime.now().plusDays(1).plusHours(3),
                LocalDateTime.now().plusDays(1).plusHours(5),
                32.0
        );
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(s2)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateShowtimeWithDifferentMovieAndThenDelete() throws Exception {
        // Add M1 & M2
        Movie m1 = movieRepository.save(new Movie("M1", "Action", 120, 7.5, 2021));
        Movie m2 = movieRepository.save(new Movie("M2", "Drama", 130, 8.2, 2022));

        // Add Showtime with M1
        ShowtimeRequest s1 = new ShowtimeRequest(
                m1.getId(), "Theater 1",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusMinutes(120),
                25.0
        );

        MvcResult result = mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(s1)))
                .andExpect(status().isOk())
                .andReturn();

        Number idNumber = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        long showtimeId = idNumber.longValue();

        // Update showtime with M2
        ShowtimeRequest updated = new ShowtimeRequest(
                m2.getId(), "Theater 1",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusMinutes(130),
                30.0
        );

        mockMvc.perform(post("/showtimes/update/" + showtimeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk());

        // Delete showtime
        mockMvc.perform(delete("/showtimes/" + showtimeId))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateBookingAfterShowtimeAdded() throws Exception {
        // Add movie M1
        Movie m1 = movieRepository.save(new Movie("M1", "Action", 100, 8.0, 2020));

        // Add showtime
        Showtime s = showtimeRepository.save(new Showtime(
                m1.getId(), "Theater 1",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusMinutes(100),
                40.0
        ));

        // Create booking
        BookingRequest booking = new BookingRequest(s.getId(), 5, UUID.randomUUID());

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateShowtimeAfterBookingToDifferentMovie() throws Exception {
        // Add M1 & M2
        Movie m1 = movieRepository.save(new Movie("M1", "Action", 100, 7.5, 2019));
        Movie m2 = movieRepository.save(new Movie("M2", "Thriller", 120, 8.1, 2023));

        // Add showtime with M1
        Showtime s = showtimeRepository.save(new Showtime(
                m1.getId(), "Theater 1",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusMinutes(100),
                35.0
        ));

        // Booking for that showtime
        BookingRequest booking = new BookingRequest(s.getId(), 10, UUID.randomUUID());
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk());

        // Update showtime to use M2 instead
        ShowtimeRequest updated = new ShowtimeRequest(
                m2.getId(), "Theater 1",
                s.getStartTime(),
                s.getEndTime().plusMinutes(20),
                37.5
        );

        mockMvc.perform(post("/showtimes/update/" + s.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk());

        Showtime updatedShowtime = showtimeRepository.findById(s.getId()).orElseThrow();
        assertEquals(m2.getId(), updatedShowtime.getMovieId());
    }
}
