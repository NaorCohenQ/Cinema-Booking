package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Services.MovieServiceAPI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Models.Showtime;
import com.att.tdp.popcorn_palace.Repositories.ShowtimeRepository;
import com.att.tdp.popcorn_palace.Services.MovieServiceAPI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MovieDataInitializer implements CommandLineRunner {

    private final MovieServiceAPI movieService;
    private final ShowtimeRepository showtimeRepository;

    public MovieDataInitializer(MovieServiceAPI movieService, ShowtimeRepository showtimeRepository) {
        this.movieService = movieService;
        this.showtimeRepository = showtimeRepository;
    }

    @Override
    public void run(String... args) {
        if (movieService.getAllMovies().isEmpty()) {
            movieService.addMovie(new Movie("Inception", "Sci-Fi", 148, "9.0", 2010));
            movieService.addMovie(new Movie("The Dark Knight", "Action", 152, "9.0", 2008));
            movieService.addMovie(new Movie("Interstellar", "Sci-Fi", 169, "8.6", 2014));
            System.out.println("Sample movies inserted!");
        }

        List<Movie> movies = movieService.getAllMovies();

        if (showtimeRepository.findAll().isEmpty() && !movies.isEmpty()) {
            showtimeRepository.save(new Showtime(
                    movies.get(0).getId(),
                    "Main Theater",
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(1).plusHours(2),
                    45.0
            ));

            showtimeRepository.save(new Showtime(
                    movies.get(1).getId(),
                    "Main Theater",
                    LocalDateTime.now().plusDays(2),
                    LocalDateTime.now().plusDays(2).plusHours(3),
                    50.0
            ));

            showtimeRepository.save(new Showtime(
                    movies.get(2).getId(),
                    "VIP Hall",
                    LocalDateTime.now().plusDays(3),
                    LocalDateTime.now().plusDays(3).plusHours(2),
                    60.0
            ));

            System.out.println("Sample showtimes inserted!");
        }
    }
}
