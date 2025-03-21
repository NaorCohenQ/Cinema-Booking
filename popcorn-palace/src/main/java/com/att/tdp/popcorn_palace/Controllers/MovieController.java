package com.att.tdp.popcorn_palace.Controllers;

import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Services.MovieServiceAPI;
import org.springframework.http.HttpStatus;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieServiceAPI movieService;

    public MovieController(MovieServiceAPI movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PostMapping
    public ResponseEntity<?> addMovie(@RequestBody Movie movie) {
        try {
            Movie savedMovie = movieService.addMovie(movie);
            return ResponseEntity.ok(savedMovie);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<Movie> updateMovie(@PathVariable String movieTitle, @RequestBody Movie movie) {
        return ResponseEntity.ok(movieService.updateMovie(movieTitle, movie));
    }

    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<String> deleteMovie(@PathVariable String movieTitle) {
        movieService.deleteMovie(movieTitle);
        return ResponseEntity.ok("Movie deleted successfully");
    }
}