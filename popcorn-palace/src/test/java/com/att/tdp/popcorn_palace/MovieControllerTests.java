package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.Conflicts.ErrorMessages;
import com.att.tdp.popcorn_palace.DTO.MovieRequest;
import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Models.Showtime;
import com.att.tdp.popcorn_palace.Repositories.MovieRepository;
import com.att.tdp.popcorn_palace.Services.MovieServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieServiceImpl movieService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
        movieService.clearCache();
    }

    @Test
    void testAddMovie_Success() throws Exception {
        MovieRequest newMovie = new MovieRequest("Avatar", "Fantasy", 162, "8.0", 2009);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Avatar"))
                .andExpect(jsonPath("$.genre").value("Fantasy"))
                .andExpect(jsonPath("$.duration").value(162))
                .andExpect(jsonPath("$.rating").value("8.0"))
                .andExpect(jsonPath("$.releaseYear").value(2009));

        Movie savedMovie = movieRepository.findByTitle("Avatar").orElse(null);
        assertNotNull(savedMovie);
        assertEquals("Avatar", savedMovie.getTitle());

        Movie fromCache = movieService.getMovieByTitle("Avatar");
        assertNotNull(fromCache);
    }

    @Test
    void testAddMovie_MissingRequiredFields() throws Exception {
            MovieRequest invalidMovie = new MovieRequest();

            mockMvc.perform(post("/movies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidMovie)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Validation error")));
        }

    @Test
    void testAddMovie_InvalidDuration() throws Exception {
        MovieRequest invalidMovie = new MovieRequest("Bad Movie", "Horror", -10, "5.0", 2022);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(ErrorMessages.DURATION_INVALID)));
    }


//    @Test
//    void testAddMovie_InvalidReleaseYear() throws Exception {
//        MovieRequest invalidMovie = new MovieRequest("Old Movie", "Drama", 120, "7.5", -2);
//
//        mockMvc.perform(post("/movies")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidMovie)))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string(containsString("")));
//    }


    @Test
    void testAddMovie_Duplicate() throws Exception {
        MovieRequest movie = new MovieRequest("Titanic", "Drama", 195, "9.0", 1997);
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk());

        // Try adding again
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Movie titled: Titanic already exists!"));
    }

    @Test
    void testUpdateMovie_Success() throws Exception {
        MovieRequest movie = new MovieRequest("Avatar", "Fantasy", 162, "8.0", 2009);
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk());

        MovieRequest updatedMovie = new MovieRequest("Avatar", "Fantasy", 162, "8.5", 2009);
        mockMvc.perform(post("/movies/update/Avatar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value("8.5"));

        Movie fromRepo = movieRepository.findByTitle("Avatar").orElse(null);
        assertNotNull(fromRepo);
        assertEquals("8.5", fromRepo.getRating());

        Movie fromCache = movieService.getMovieByTitle("Avatar");
        assertNotNull(fromCache);
        assertEquals("8.5", fromCache.getRating());
    }

    @Test
    void testUpdateMovie_NotFound() throws Exception {
        MovieRequest updatedMovie = new MovieRequest("NonExisting", "Sci-Fi", 120, "7.5", 2023);

        mockMvc.perform(post("/movies/update/NonExisting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Movie named: NonExisting not found!"));
    }

    @Test
    void testDeleteMovie_RemovesFromDBAndCache() throws Exception {
        MovieRequest movie = new MovieRequest("Matrix", "Action", 136, "8.7", 1999);
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/movies/Matrix"))
                .andExpect(status().isOk());

        assertFalse(movieRepository.findByTitle("Matrix").isPresent());
        assertThrows(EntityNotFoundException.class, () -> movieService.getMovieByTitle("Matrix"));
    }

    @Test
    void testAddMovie_InvalidDurationFormat_ShouldReturn400() throws Exception {
        String invalidJson = """
        {
            "title": "Matrix",
            "genre": "Action",
            "duration": "long",
            "rating": "R",
            "releaseYear": 1999
        }
    """;

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid")));
    }

//    @Test
//    void testDeleteMovie_WithShowtimes_Fails() throws Exception {
//        Movie movie = movieRepository.save(new Movie("Blade Runner", "Sci-Fi", 117, "8.1", 1982));
//        showtimeRepository.save(new Showtime(
//                movie.getId(),
//                "Retro Hall",
//                LocalDateTime.now().plusHours(2),
//                LocalDateTime.now().plusHours(4),
//                25.0
//        ));
//
//        mockMvc.perform(delete("/movies/" + movie.getId()))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string(containsString("existing showtimes")));
//    }


}
