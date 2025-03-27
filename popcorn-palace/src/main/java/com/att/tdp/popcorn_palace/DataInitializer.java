//package com.att.tdp.popcorn_palace;
//
//import com.att.tdp.popcorn_palace.DTO.MovieRequest;
//import com.att.tdp.popcorn_palace.Models.Booking;
//import com.att.tdp.popcorn_palace.Models.Movie;
//import com.att.tdp.popcorn_palace.Models.Showtime;
//import com.att.tdp.popcorn_palace.Repositories.BookingRepository;
//import com.att.tdp.popcorn_palace.Repositories.ShowtimeRepository;
//import com.att.tdp.popcorn_palace.Services.MovieServiceAPI;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    private final MovieServiceAPI movieService;
//    private final ShowtimeRepository showtimeRepository;
//    private final BookingRepository bookingRepository;
//
//    public DataInitializer(MovieServiceAPI movieService, ShowtimeRepository showtimeRepository, BookingRepository bookingRepository) {
//        this.movieService = movieService;
//        this.showtimeRepository = showtimeRepository;
//        this.bookingRepository = bookingRepository;
//    }
//
//    @Override
//    public void run(String... args) {
//        if (movieService.getAllMovies().isEmpty()) {
//            movieService.addMovie(new MovieRequest("Oppenheimer", "Biography", 180, "8.6", 2023));
//            movieService.addMovie(new MovieRequest("Inception", "Sci-Fi", 148, "9.0", 2010));
//            movieService.addMovie(new MovieRequest("The Dark Knight", "Action", 152, "9.0", 2008));
//            movieService.addMovie(new MovieRequest("Interstellar", "Sci-Fi", 169, "8.6", 2014));
//            movieService.addMovie(new MovieRequest("Titanic", "Drama", 195, "7.8", 1997));
//            movieService.addMovie(new MovieRequest("Avatar", "Sci-Fi", 162, "7.9", 2009));
//            movieService.addMovie(new MovieRequest("The Godfather", "Crime", 175, "9.2", 1972));
//            movieService.addMovie(new MovieRequest("Pulp Fiction", "Crime", 154, "8.9", 1994));
//        }
//
//        List<Movie> movies = movieService.getAllMovies();
//
//        if (showtimeRepository.findAll().isEmpty() && !movies.isEmpty()) {
//            LocalDateTime now = LocalDateTime.now();
//
//            showtimeRepository.save(new Showtime(movies.get(0).getId(), "Theater 1", now.plusDays(1), now.plusDays(1).plusMinutes(148), 45.0));
//            showtimeRepository.save(new Showtime(movies.get(1).getId(), "Theater 1", now.plusDays(1), now.plusDays(1).plusMinutes(148), 45.0));
//            showtimeRepository.save(new Showtime(movies.get(2).getId(), "Theater 2", now.plusDays(2), now.plusDays(2).plusMinutes(152), 50.0));
//            showtimeRepository.save(new Showtime(movies.get(3).getId(), "Theater 3", now.plusDays(3), now.plusDays(3).plusMinutes(169), 60.0));
//            showtimeRepository.save(new Showtime(movies.get(4).getId(), "Theater 4", now.plusDays(1), now.plusDays(1).plusMinutes(180), 70.0));
//        }
//
////        List<Showtime> showtimes = showtimeRepository.findAll();
////
////        if (bookingRepository.findAll().isEmpty() && !showtimes.isEmpty()) {
////            bookingRepository.save(new Booking(showtimes.get(0).getId(), 5, UUID.randomUUID()));
////            bookingRepository.save(new Booking(showtimes.get(1).getId(), 10, UUID.randomUUID()));
//        }
//    }
