package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Repositories.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
    }

    @Test
    void testAddMovie_Success() throws Exception {
        Movie newMovie = new Movie("Avatar", "Fantasy", 162, "8.0", 2009);

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
        assertNotNull(savedMovie, "Movie should be saved in the database");
        assertEquals("Avatar", savedMovie.getTitle());
        assertEquals("Fantasy", savedMovie.getGenre());
    }

    @Test
    void testAddMovie_MissingRequiredFields() throws Exception {
        Movie invalidMovie = new Movie(); // Empty movie

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Movie title cannot be empty!"));
    }

    @Test
    void testAddMovie_InvalidDuration() throws Exception {
        Movie invalidMovie = new Movie("Bad Movie", "Horror", -10, "5.0", 2022);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Duration must be greater than 0!"));
    }

    @Test
    void testAddMovie_InvalidReleaseYear() throws Exception {
        Movie invalidMovie = new Movie("Old Movie", "Drama", 120, "7.5", 1800);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid release year!"));
    }

    @Test
    void testAddMovie_Duplicate() throws Exception {
        // Insert the movie first
        Movie existingMovie = new Movie("Titanic", "Drama", 195, "9.0", 1997);
        movieRepository.save(existingMovie);

        // Try adding the same movie again
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingMovie)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Movie titled: "+existingMovie.getTitle()+" already exists!"));
    }

    @Test
    void testUpdateMovie_Success() throws Exception {
        // Add movie first
        Movie existingMovie = new Movie("Avatar", "Fantasy", 162, "8.0", 2009);
        movieRepository.save(existingMovie);

        // Updated details
        Movie updatedMovie = new Movie("Avatar", "Fantasy", 162, "8.5", 2009);

        mockMvc.perform(post("/movies/update/Avatar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Avatar"))
                .andExpect(jsonPath("$.rating").value("8.5"));
    }

    @Test
    void testUpdateMovie_NotFound() throws Exception {
        Movie updatedMovie = new Movie("NonExisting", "Sci-Fi", 120, "7.5", 2023);

        mockMvc.perform(post("/movies/update/NonExisting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Movie named: "+updatedMovie.getTitle()+" not found!"));
    }
}
