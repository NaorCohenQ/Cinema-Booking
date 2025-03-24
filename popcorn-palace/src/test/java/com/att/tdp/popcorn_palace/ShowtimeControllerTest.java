package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.DTO.ShowtimeRequest;
import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Models.Showtime;
import com.att.tdp.popcorn_palace.Repositories.MovieRepository;
import com.att.tdp.popcorn_palace.Repositories.ShowtimeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ShowtimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        showtimeRepository.deleteAll();
        movieRepository.deleteAll();
    }

    @Test
    void testAddShowtime_Success() throws Exception {
        Movie movie = new Movie("Inception", "Sci-Fi", 148, "9.0", 2010);
        movie = movieRepository.save(movie);

        ShowtimeRequest request = new ShowtimeRequest(
                movie.getId(),
                "Main Theater",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3),
                45.0
        );

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theater").value("Main Theater"))
                .andExpect(jsonPath("$.price").value(45.0));
    }

    @Test
    void testAddShowtime_Overlapping() throws Exception {
        Movie movie = movieRepository.save(new Movie("Avatar", "Sci-Fi", 162, "8.0", 2009));
        Showtime existing = new Showtime(
                movie.getId(), "Main Theater",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3),
                40.0
        );
        showtimeRepository.save(existing);

        ShowtimeRequest request = new ShowtimeRequest(
                movie.getId(),
                "Main Theater",
                LocalDateTime.now().plusHours(2),  // Overlaps
                LocalDateTime.now().plusHours(4),
                50.0
        );

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("overlaps")));
    }

    @Test
    void testGetShowtimeById_NotFound() throws Exception {
        mockMvc.perform(get("/showtimes/9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")));
    }

    @Test
    void testAddShowtime_InvalidFields() throws Exception {
        ShowtimeRequest request = new ShowtimeRequest(
                null, "", null, null, -5.0
        );

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid movie ID")));
    }

    @Test
    void testUpdateShowtime_Success() throws Exception {
        Movie movie = movieRepository.save(new Movie("Interstellar", "Sci-Fi", 169, "8.6", 2014));
        Showtime showtime = showtimeRepository.save(new Showtime(
                movie.getId(), "VIP Hall",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                60.0
        ));

        ShowtimeRequest updateRequest = new ShowtimeRequest(
                movie.getId(), "VIP Hall",
                LocalDateTime.now().plusDays(1).plusHours(1),
                LocalDateTime.now().plusDays(1).plusHours(3),
                70.0
        );

        mockMvc.perform(post("/showtimes/update/" + showtime.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(70.0));
    }

    @Test
    void testUpdateShowtime_OverlappingFails() throws Exception {
        Movie movie = movieRepository.save(new Movie("Tenet", "Sci-Fi", 150, "7.5", 2020));
        Showtime first = showtimeRepository.save(new Showtime(
                movie.getId(), "Main Theater",
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(4),
                40.0
        ));

        Showtime second = showtimeRepository.save(new Showtime(
                movie.getId(), "Main Theater",
                LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(7),
                50.0
        ));

        ShowtimeRequest overlappingUpdate = new ShowtimeRequest(
                movie.getId(), "Main Theater",
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(6),
                55.0
        );

        mockMvc.perform(post("/showtimes/update/" + second.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overlappingUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("overlaps")));
    }

    @Test
    void testDeleteShowtime_Success() throws Exception {
        Movie movie = movieRepository.save(new Movie("Dunkirk", "War", 106, "7.9", 2017));
        Showtime showtime = showtimeRepository.save(new Showtime(
                movie.getId(), "Small Theater",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(2),
                35.0
        ));

        mockMvc.perform(delete("/showtimes/" + showtime.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteShowtime_NotFound() throws Exception {
        mockMvc.perform(delete("/showtimes/99999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")));
    }


    //----- might be duplicates ---

    @Test
    void testUpdateShowtime_Success2() throws Exception {
        Movie movie = movieRepository.save(new Movie("Matrix", "Action", 136, "8.7", 1999));
        Showtime existing = showtimeRepository.save(new Showtime(
                movie.getId(), "Main Theater",
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(4),
                35.0
        ));

        ShowtimeRequest updated = new ShowtimeRequest(
                movie.getId(),
                "Main Theater",
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(5),
                42.0
        );

        mockMvc.perform(post("/showtimes/update/" + existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(42.0));
    }

    @Test
    void testUpdateShowtime_Overlapping() throws Exception {
        Movie movie = movieRepository.save(new Movie("Batman", "Action", 120, "7.9", 2005));

        showtimeRepository.save(new Showtime(
                movie.getId(), "VIP Hall",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3),
                30.0
        ));

        Showtime toUpdate = showtimeRepository.save(new Showtime(
                movie.getId(), "VIP Hall",
                LocalDateTime.now().plusHours(4),
                LocalDateTime.now().plusHours(6),
                28.0
        ));

        ShowtimeRequest overlapping = new ShowtimeRequest(
                movie.getId(),
                "VIP Hall",
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(5),
                33.0
        );

        mockMvc.perform(post("/showtimes/update/" + toUpdate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overlapping)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("overlaps")));
    }

    @Test
    void testUpdateShowtime_InvalidMovieId() throws Exception {
        Movie movie = movieRepository.save(new Movie("Spider-Man", "Action", 130, "7.2", 2012));

        Showtime showtime = showtimeRepository.save(new Showtime(
                movie.getId(), "Cinema City",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3),
                30.0
        ));

        ShowtimeRequest invalidMovie = new ShowtimeRequest(
                9999L,
                "Cinema City",
                LocalDateTime.now().plusHours(4),
                LocalDateTime.now().plusHours(6),
                50.0
        );

        mockMvc.perform(post("/showtimes/update/" + showtime.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Movie with ID " + invalidMovie.getMovieId() + " not found!")));
    }

    @Test
    void testDeleteShowtime_Success2() throws Exception {
        Movie movie = movieRepository.save(new Movie("John Wick", "Action", 110, "8.2", 2014));

        Showtime showtime = showtimeRepository.save(new Showtime(
                movie.getId(), "IMAX Theater",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3),
                60.0
        ));

        mockMvc.perform(delete("/showtimes/" + showtime.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteShowtime_NotFound2() throws Exception {
        mockMvc.perform(delete("/showtimes/9876"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")));
    }

    @Test
    void testGetShowtimeById_Success() throws Exception {
        Movie movie = movieRepository.save(new Movie("Dune", "Sci-Fi", 155, "8.1", 2021));

        Showtime showtime = showtimeRepository.save(new Showtime(
                movie.getId(), "Sci-Fi Hall",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3),
                55.0
        ));

        mockMvc.perform(get("/showtimes/" + showtime.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theater").value("Sci-Fi Hall"))
                .andExpect(jsonPath("$.price").value(55.0));
    }
}
