package com.att.tdp.popcorn_palace.Services;
import com.att.tdp.popcorn_palace.Models.Movie;

import java.util.List;

public interface MovieServiceAPI {
    List<Movie> getAllMovies();
    Movie addMovie(Movie movie);
    Movie updateMovie(String movieTitle, Movie movie);
    void deleteMovie(String movieTitle);
}

