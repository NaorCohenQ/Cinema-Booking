package com.att.tdp.popcorn_palace.Services;

import com.att.tdp.popcorn_palace.DTO.MovieRequest;
import com.att.tdp.popcorn_palace.Models.Movie;
import com.att.tdp.popcorn_palace.Repositories.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MovieServiceImpl implements MovieServiceAPI {

    private static final Logger logger = LoggerFactory.getLogger(MovieServiceImpl.class);

    private final MovieRepository movieRepository;

    private final ConcurrentHashMap<String, Movie> _allMovies = new ConcurrentHashMap<>();
    private boolean isAllMoviesLoaded = false;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public List<Movie> getAllMovies() {
        if (!isAllMoviesLoaded) {
            logger.info("⏬ Loading all movies from DB...");
            List<Movie> allMoviesList = movieRepository.findAll();
            for (Movie movie : allMoviesList) {
                _allMovies.put(movie.getTitle(), movie);
            }
            isAllMoviesLoaded = true;
        } else {
            logger.info("✅ Returning all movies from cache");
        }
        return _allMovies.values().stream().toList();
    }



    public void clearCache() {
        _allMovies.clear();
        isAllMoviesLoaded = false;
    }
    @Override
    public Movie addMovie(MovieRequest movieDTO) {
        if (movieDTO == null) {
            throw new IllegalArgumentException("Movie request body is missing!");
        }
        validateMovieFields(movieDTO);


        if (validateIfMovieExists(movieDTO.getTitle())) {
            logger.warn("❌ Attempt to add existing movie: {}", movieDTO.getTitle());
            throw new IllegalArgumentException("Movie titled: " + movieDTO.getTitle() + " already exists!");
        }

        validateMovieFields(movieDTO);
        Movie newMovie = new Movie(movieDTO);
        Movie savedMovie = movieRepository.save(newMovie);
        _allMovies.put(savedMovie.getTitle(), savedMovie);
        logger.info("✅ Movie added: {}", savedMovie.getTitle());
        return savedMovie;
    }

    @Override
    public Movie updateMovie(String movieTitle, MovieRequest movieDTO) {
        Movie reqMovie = getMovieIfExists(movieTitle);
        validateMovieFields(movieDTO);
        reqMovie.updateDetails(movieDTO);
        Movie savedMovie = movieRepository.save(reqMovie);
        _allMovies.put(savedMovie.getTitle(), savedMovie);

        logger.info("✅ Movie updated: {}", savedMovie.getTitle());
        return savedMovie;
    }

    @Override
    @Transactional
    public void deleteMovie(String movieTitle) {
        if (movieTitle != null && !validateIfMovieExists(movieTitle)) {
            logger.warn("❌ Attempt to delete non-existent movie: {}", movieTitle);
            throw new EntityNotFoundException("Delete Movie Operation Failed! Movie named: " + movieTitle + " not found!");
        }

        movieRepository.deleteByTitle(movieTitle);
        _allMovies.remove(movieTitle);
        logger.info("🗑️ Movie deleted: {}", movieTitle);
    }

    @Override
    public Movie getMovieByTitle(String title) {
        return getMovieIfExists(title);
    }

    @Override
    public boolean validateIfMovieExists(Long movieID) {
            if (!movieRepository.existsById(movieID)) {
                throw new EntityNotFoundException("Movie with ID " + movieID + " not found!");
            }
            return true;
    }

    private Movie getMovieIfExists(String movieTitle) {
        if (_allMovies.containsKey(movieTitle)) {
            logger.info("✅ Fetched movie from cache: {}", movieTitle);
            return _allMovies.get(movieTitle);
        }

        logger.info("⏬ Fetching movie from DB: {}", movieTitle);
        Optional<Movie> movieOpt = movieRepository.findByTitle(movieTitle);
        if (movieOpt.isEmpty()) {
            logger.error("❌ Movie not found: {}", movieTitle);
            throw new EntityNotFoundException("Movie named: " + movieTitle + " not found!");
        }

        Movie movie = movieOpt.get();
        _allMovies.put(movieTitle, movie);
        return movie;
    }

    private void validateMovieFields(MovieRequest movie) {
        if (movie.getTitle() == null || movie.getTitle().isBlank()) {
            throw new IllegalArgumentException("Movie title cannot be empty!");
        }
        if (movie.getGenre() == null || movie.getGenre().isBlank()) {
            throw new IllegalArgumentException("Genre cannot be empty!");
        }
        if (movie.getDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0!");
        }
        if (movie.getRating() == null || movie.getRating().isBlank()) {
            throw new IllegalArgumentException("Rating cannot be empty!");
        }
        if (movie.getReleaseYear() < 1900 || movie.getReleaseYear() > 2100) {
            throw new IllegalArgumentException("Invalid release year!");
        }
    }

    private boolean validateIfMovieExists(String movieTitle) {
        return _allMovies.containsKey(movieTitle) || movieRepository.findByTitle(movieTitle).isPresent();
    }
}
