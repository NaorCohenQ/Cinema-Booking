package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Services.MovieServiceAPI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MovieDataInitializer implements CommandLineRunner {
    private final MovieServiceAPI movieService;

    public MovieDataInitializer(MovieServiceAPI movieService) {
        this.movieService = movieService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (movieService.getAllMovies().isEmpty()) {
            movieService.addMovie(new Movie("Inception", "Sci-Fi", 148, "9.0", 2010));
            movieService.addMovie(new Movie("The Dark Knight", "Action", 152, "9.0", 2008));
            movieService.addMovie(new Movie("Interstellar", "Sci-Fi", 169, "8.6", 2014));
            System.out.println("Sample movies inserted!");
        }
    }
}
