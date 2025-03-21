package com.att.tdp.popcorn_palace.Services;

import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Repositories.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieServiceAPI {
    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }


    @Override
    public Movie addMovie(Movie movie) {
        if(validateIfMovieExists(movie.getTitle())){
            throw new IllegalArgumentException("Movie titled: "+movie.getTitle()+" already exists!");
        }

        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Movie title cannot be empty!");
        }
        if (movie.getGenre() == null || movie.getGenre().trim().isEmpty()) {
            throw new IllegalArgumentException("Genre cannot be empty!");
        }
        if (movie.getDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0!");
        }
        if (movie.getRating() == null || movie.getRating().trim().isEmpty()) {
            throw new IllegalArgumentException("Rating cannot be empty!");
        }
        if (movie.getReleaseYear() < 1900 || movie.getReleaseYear() > 2100) {
            throw new IllegalArgumentException("Invalid release year!");
        }

        return movieRepository.save(movie);
    }


    @Override
    public Movie updateMovie(String movieTitle, Movie movie) {
        Movie reqMovie = getMovieIfExists(movieTitle);
            reqMovie.setTitle(movie.getTitle());
            reqMovie.setGenre(movie.getGenre());
            reqMovie.setDuration(movie.getDuration());
            reqMovie.setRating(movie.getRating());
            reqMovie.setReleaseYear(movie.getReleaseYear());
            return movieRepository.save(reqMovie);
        }

    private Movie getMovieIfExists(String movieTitle) {
        Optional<Movie> existingMovie = movieRepository.findByTitle(movieTitle);
        if (existingMovie.isEmpty()) {
            throw new EntityNotFoundException("Movie named: "+movieTitle+" not found!");
        }
        return existingMovie.get();
    }

    private boolean validateIfMovieExists(String movieTitle) {
        Optional<Movie> existingMovie = movieRepository.findByTitle(movieTitle);
     if (existingMovie.isPresent())
        return true;
     return false;
    }

    @Override
    public void deleteMovie(String movieTitle) {
        if (!validateIfMovieExists(movieTitle)){
            throw new EntityNotFoundException("Delete Movie Operation Failed !  Movie named: "+movieTitle +" not found!");
        }
        movieRepository.deleteByTitle(movieTitle);
    }
}
