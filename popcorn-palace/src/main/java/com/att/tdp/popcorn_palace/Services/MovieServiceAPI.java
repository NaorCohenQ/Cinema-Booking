package com.att.tdp.popcorn_palace.Services;
import com.att.tdp.popcorn_palace.DTO.MovieRequest;
import com.att.tdp.popcorn_palace.Models.Movie;

import java.util.List;

public interface MovieServiceAPI {
    List<Movie> getAllMovies();
    Movie addMovie(MovieRequest movie);
    Movie updateMovie(String movieTitle, MovieRequest movie);
    void deleteMovie(String movieTitle);
    Movie getMovieByTitle(String title);
    boolean validateIfMovieExists(Long movieID);
}

