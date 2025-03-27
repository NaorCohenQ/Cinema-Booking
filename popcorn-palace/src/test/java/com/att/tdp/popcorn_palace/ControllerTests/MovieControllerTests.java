package com.att.tdp.popcorn_palace.ControllerTests;

import com.att.tdp.popcorn_palace.Conflicts.ErrorMessages;
import com.att.tdp.popcorn_palace.DTO.MovieRequest;
import com.att.tdp.popcorn_palace.Models.Movie;
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

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerTests {

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
        MovieRequest newMovie = new MovieRequest("Avatar", "Fantasy", 162, 8.0, 2009);

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

        Movie fromService = movieService.getMovieByTitle("Avatar");
        assertNotNull(fromService);
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
        MovieRequest invalidMovie = new MovieRequest("Bad Movie", "Horror", -10, 5.0, 2022);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(ErrorMessages.DURATION_INVALID)));
    }

    @Test
    void testAddMovie_InvalidRating() throws Exception {
        MovieRequest invalidMovie = new MovieRequest("Gladiator", "Action", 120, -1.0, 2000);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(ErrorMessages.RATING_REQUIRED)));
    }


    @Test
    void testAddMovie_EmptyTitleAndGenre() throws Exception {
        MovieRequest invalidMovie = new MovieRequest("", "", 120, 7.5, 2023);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddMovie_EmptyGenre() throws Exception {
        MovieRequest invalidMovie = new MovieRequest("Interstellar", "", 169, 8.6, 2014);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(ErrorMessages.GENRE_REQUIRED)));
    }

    @Test
    void testAddMovie_EmptyTitle() throws Exception {
        MovieRequest invalidMovie = new MovieRequest("", "Sci-Fi", 169, 8.6, 2014);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(ErrorMessages.MOVIE_TITLE_REQUIRED)));
    }


//    @Test
//    void testAddMovie_InvalidReleaseYear() throws Exception {
//        MovieRequest invalidMovie = new MovieRequest("Old Movie", "Drama", 120, 7.5, -2);
//
//        mockMvc.perform(post("/movies")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidMovie)))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string(ErrorMessages.));
//    }

    @Test
    void testAddMovie_NullFields() throws Exception {
        String nullJson = """
        {
            "title": null,
            "genre": null,
            "duration": null,
            "rating": null,
            "releaseYear": null
        }
    """;

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testAddMovie_Duplicate() throws Exception {
        MovieRequest movie = new MovieRequest("Titanic", "Drama", 195, 9.0, 1997);
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk());

        // Try adding again
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Movie titled: Titanic already exists!"));
    }

    @Test
    void testUpdateMovie_Success() throws Exception {
        MovieRequest movie = new MovieRequest("Avatar", "Fantasy", 162, 8.0, 2009);
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk());

        MovieRequest updatedMovie = new MovieRequest("Avatar", "Fantasy", 162, 8.5, 2009);
        mockMvc.perform(post("/movies/update/Avatar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(8.5));

        Movie fromRepo = movieRepository.findByTitle("Avatar").orElse(null);
        assertNotNull(fromRepo);
        assertEquals(8.5, fromRepo.getRating());
    }

    @Test
    void testUpdateMovie_NotFound() throws Exception {
        MovieRequest updatedMovie = new MovieRequest("NonExisting", "Sci-Fi", 120, 7.5, 2023);

        mockMvc.perform(post("/movies/update/NonExisting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Movie named: NonExisting not found!"));
    }

    @Test
    void testDeleteMovie_success() throws Exception {
        MovieRequest movie = new MovieRequest("Matrix", "Action", 136, 8.7, 1999);
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
    void testDeleteMovie_NonExisting_ShouldReturn404() throws Exception {
        String nonExistingTitle = "NonExistentMovie";

        mockMvc.perform(delete("/movies/" + nonExistingTitle))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Movie named: " + nonExistingTitle + " not found")));
    }


    @Test
    void testAddMovie_InvalidDurationFormat_ShouldReturn400() throws Exception {
        String invalidJson = """
        {
            "title": "Matrix",
            "genre": "Action",
            "duration": "long",
            "rating": 8,
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

    @Test
    void testUpdateMovie_InvalidDuration() throws Exception {
        MovieRequest invalidUpdate = new MovieRequest("Avatar", "Fantasy", -10, 8.0, 2009);

        mockMvc.perform(post("/movies/update/Avatar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(ErrorMessages.DURATION_INVALID)));
    }

    @Test
    void testUpdateMovie_BlankTitle() throws Exception {
        MovieRequest invalidUpdate = new MovieRequest("", "Fantasy", 120, 8.0, 2009);

        mockMvc.perform(post("/movies/update/Avatar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(ErrorMessages.MOVIE_TITLE_REQUIRED)));
    }

    @Test
    void testUpdateMovie_BlankGenre() throws Exception {
        MovieRequest invalidUpdate = new MovieRequest("Avatar", "", 120, 8.0, 2009);

        mockMvc.perform(post("/movies/update/Avatar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(ErrorMessages.GENRE_REQUIRED)));
    }

    @Test
    void testUpdateMovie_InvalidRatingFormat_ShouldReturn400() throws Exception {
        String invalidJson = """
        {
            "title": "Avatar",
            "genre": "Fantasy",
            "duration": 120,
            "rating": "Excellent",
            "releaseYear": 2009
        }
    """;

        mockMvc.perform(post("/movies/update/Avatar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid")));
    }

    @Test
    void testUpdateMovie_InvalidGenreFormat() throws Exception {

        MovieRequest newMovie = new MovieRequest("Avatar", "Fantasy", 162, 8.0, 2009);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMovie)))
                .andExpect(status().isOk());

        String invalidJson = """
        {
            "title": "Avatar",
            "genre": 123,
            "duration": 120,
            "rating": -8.0,
            "releaseYear": 2009
        }
    """;

        mockMvc.perform(post("/movies/update/Avatar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Validation error")));
    }

    @Test
    void testUpdateMovie_NullFields() throws Exception {
        String nullJson = """
        {
            "title": null,
            "genre": null,
            "duration": null,
            "rating": null,
            "releaseYear": null
        }
    """;

        mockMvc.perform(post("/movies/update/Avatar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullJson))
                .andExpect(status().isBadRequest());
    }

}
