package com.att.tdp.popcorn_palace.UnitTest;

import com.att.tdp.popcorn_palace.DTO.ShowtimeRequest;
import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Models.Showtime;
import com.att.tdp.popcorn_palace.Repositories.ShowtimeRepository;
import com.att.tdp.popcorn_palace.Services.MovieServiceAPI;
import com.att.tdp.popcorn_palace.Services.ShowtimeServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowtimeServiceTests {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private MovieServiceAPI movieServiceAPI;

    @InjectMocks
    private ShowtimeServiceImpl showtimeService;

    private Showtime showtime;
    private ShowtimeRequest showtimeRequest;
    private Long movieId = 100L;

    @BeforeEach
    void setUp() {
        showtimeRequest = new ShowtimeRequest(
                movieId,
                "Room 1",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusMinutes(180),
                45.0
        );

        showtime = new Showtime(showtimeRequest);
        showtime.setId(1L);
    }

    @Test
    void getAllShowtimes_shouldReturnList() {
        when(showtimeRepository.findAll()).thenReturn(List.of(showtime));

        List<Showtime> result = showtimeService.getAllShowtimes();

        assertThat(result).containsExactly(showtime);
    }

    @Test
    void getShowtimeById_shouldReturnShowtime_whenExists() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));

        Showtime result = showtimeService.getShowtimeById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getShowtimeById_shouldThrow_whenNotExists() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> showtimeService.getShowtimeById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Showtime not found");
    }

    @Test
    void addShowtime_shouldSucceed_whenValid() {
        Movie movie = new Movie();
        movie.setDuration(180);

        when(movieServiceAPI.validateIfMovieExists(movieId)).thenReturn(true);
        when(movieServiceAPI.getMovieIfExists(movieId)).thenReturn(movie);
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(showtime);
        when(showtimeRepository.findByTheater("Room 1")).thenReturn(Collections.emptyList());

        Showtime result = showtimeService.addShowtime(showtimeRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void deleteShowtime_shouldSucceed_whenExists() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));

        showtimeService.deleteShowtime(1L);

        verify(showtimeRepository).deleteById(1L);
    }

    @Test
    void deleteShowtime_shouldThrow_whenNotExists() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> showtimeService.deleteShowtime(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Showtime with ID 1 not found");
    }

    @Test
    void updateShowtime_shouldSucceed_whenValid() {
        Movie movie = new Movie();
        movie.setDuration(180);

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        when(movieServiceAPI.validateIfMovieExists(movieId)).thenReturn(true);
        when(movieServiceAPI.getMovieIfExists(movieId)).thenReturn(movie);
        when(showtimeRepository.findByTheater("Room 1")).thenReturn(Collections.emptyList());
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(showtime);

        Showtime result = showtimeService.updateShowtime(1L, showtimeRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }
}
