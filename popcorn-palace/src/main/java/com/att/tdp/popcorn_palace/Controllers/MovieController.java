package com.att.tdp.popcorn_palace.Controllers;

import com.att.tdp.popcorn_palace.DTO.MovieRequest;
import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Services.MovieServiceAPI;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> addMovie(@RequestBody @Valid MovieRequest movieDTO) {
            return ResponseEntity.ok(movieService.addMovie(movieDTO));
    }

    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<Movie> updateMovie(@PathVariable String movieTitle, @RequestBody MovieRequest movieDTO) {
        return ResponseEntity.ok(movieService.updateMovie(movieTitle, movieDTO));
    }

    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<String> deleteMovie(@PathVariable String movieTitle) {
        movieService.deleteMovie(movieTitle);
        return ResponseEntity.ok("Movie deleted successfully");
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Movie> getMovieByTitle(@PathVariable String title) {
        return ResponseEntity.ok(movieService.getMovieByTitle(title));
    }
}