package com.att.tdp.popcorn_palace.UnitTest;

import com.att.tdp.popcorn_palace.DTO.MovieRequest;
import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Repositories.MovieRepository;
import com.att.tdp.popcorn_palace.Services.MovieServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class MovieServiceTests {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    private MovieRequest movieR1;
    private Movie movie;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
        movieR1 = new MovieRequest("Oppenheimer", "Biography", 180, "8.6", 2023);
        movie = new Movie(movieR1);
    }

    @Test
    void addMovie_shouldSucceed_whenMovieIsNew() {
        when(movieRepository.findByTitle(movieR1.getTitle())).thenReturn(Optional.empty());
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        Movie result = movieService.addMovie(movieR1);

        assertThat(result.getTitle()).isEqualTo(movieR1.getTitle());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void addMovie_shouldFail_whenMovieAlreadyExists() {
        when(movieRepository.findByTitle(movieR1.getTitle())).thenReturn(Optional.of(movie));

        assertThatThrownBy(() -> movieService.addMovie(movieR1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void updateMovie_shouldSucceed_whenMovieExists() {
        MovieRequest updatedRequest = new MovieRequest("Oppenheimer", "Drama", 190, "9.0", 2023);
        Movie updatedMovie = new Movie(updatedRequest);

        when(movieRepository.findByTitle(movieR1.getTitle())).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);
        Movie result = movieService.updateMovie(movieR1.getTitle(), updatedRequest);

        assertThat(result.getGenre()).isEqualTo("Drama");
        assertThat(result.getDuration()).isEqualTo(190);
        assertThat(result.getRating()).isEqualTo("9.0");
        verify(movieRepository).save(any(Movie.class));
    }


    @Test
    void updateMovie_shouldFail_whenMovieDoesNotExist() {
        when(movieRepository.findByTitle(movieR1.getTitle())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.updateMovie(movieR1.getTitle(), movieR1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void deleteMovie_shouldSucceed_whenMovieExists() {
        when(movieRepository.findByTitle(movieR1.getTitle())).thenReturn(Optional.of(movie));

        movieService.deleteMovie(movieR1.getTitle());

        verify(movieRepository).deleteByTitle(movieR1.getTitle());
    }

    @Test
    void deleteMovie_shouldFail_whenMovieDoesNotExist() {
        when(movieRepository.findByTitle(movieR1.getTitle())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.deleteMovie(movieR1.getTitle()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");

        verify(movieRepository, never()).deleteByTitle(anyString());
    }

    @Test
    void getMovieByTitle_shouldReturnMovie_whenExists() {
        when(movieRepository.findByTitle(movieR1.getTitle())).thenReturn(Optional.of(movie));

        Movie result = movieService.getMovieByTitle(movieR1.getTitle());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(movieR1.getTitle());
    }

    @Test
    void getMovieByTitle_shouldThrow_whenMovieNotExists() {
        when(movieRepository.findByTitle(movieR1.getTitle())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.getMovieByTitle(movieR1.getTitle()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void validateIfMovieExists_shouldReturnTrue_whenMovieExists() {
        when(movieRepository.existsById(1L)).thenReturn(true);

        assertThat(movieService.validateIfMovieExists(1L)).isTrue();
    }

    @Test
    void validateIfMovieExists_shouldThrow_whenMovieNotExists() {
        when(movieRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> movieService.validateIfMovieExists(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

}
