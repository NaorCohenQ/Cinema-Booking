package com.att.tdp.popcorn_palace.Services;
import com.att.tdp.popcorn_palace.Models.Showtime;
import com.att.tdp.popcorn_palace.Repositories.MovieRepository;
import com.att.tdp.popcorn_palace.Repositories.ShowtimeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeServiceImpl implements ShowtimeServiceAPI {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    public ShowtimeServiceImpl(ShowtimeRepository showtimeRepository, MovieRepository movieRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

//    @Override
//    public Showtime getShowtimeById(Long showtimeID) {
//        return getShowtime(showtimeID);
//
//    }
//
//    private Showtime getShowtime(Long showtimeID) {
//        return showtimeRepository.findById(showtimeID)
//                .orElseThrow(() -> new EntityNotFoundException("Showtime not found"));
//    }

    @Override
    public Showtime getShowtimeById(Long showtimeID) {
            return getShowTime(showtimeID);

    }

    private Showtime getShowTime(Long showtimeID) {
        return showtimeRepository.findById(showtimeID)
                .orElseThrow(() -> new EntityNotFoundException("Showtime with id: "+showtimeID+" not found"));
    }

    @Override
    public Showtime addShowtime(Showtime showtime) {
            showtimeValidation(showtime);

        if (!movieRepository.existsById(showtime.getMovieId())) {
            throw new EntityNotFoundException("Movie ID not found!");
        }

        if (isOverlapping(showtime.getTheater(), showtime.getStartTime(), showtime.getEndTime(), null)) {
            throw new IllegalArgumentException("Showtime overlaps with existing one in same theater!");
        }

        return showtimeRepository.save(showtime);
    }

    private void showtimeValidation(Showtime showtime) {
        if (showtime.getMovieId() == null || showtime.getMovieId() <= 0) {
            throw new IllegalArgumentException("Invalid movie ID.");
        }

        if (showtime.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be a negative number.");
        }

        if (showtime.getTheater() == null || showtime.getTheater().trim().isEmpty()) {
            throw new IllegalArgumentException("Theater name cannot be empty.");
        }

        if (showtime.getStartTime() == null || showtime.getEndTime() == null) {
            throw new IllegalArgumentException("Start and end time must be provided.");
        }

        if (!showtime.getEndTime().isAfter(showtime.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }


    }

    @Override
    public Showtime updateShowtime(Long id, Showtime updated) {

        if (updated.getMovieId() == null || updated.getMovieId() <= 0) {
            throw new IllegalArgumentException("Invalid movie ID.");
        }

        if (updated.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }

        if (updated.getTheater() == null || updated.getTheater().trim().isEmpty()) {
            throw new IllegalArgumentException("Theater name cannot be empty.");
        }

        if (updated.getStartTime() == null || updated.getEndTime() == null) {
            throw new IllegalArgumentException("Start and end time must be provided.");
        }

        if (!updated.getEndTime().isAfter(updated.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        Showtime existing = showtimeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Showtime not found!"));

        if (!movieRepository.existsById(updated.getMovieId())) {
            throw new EntityNotFoundException("Movie ID not found!");
        }

        if (isOverlapping(updated.getTheater(), updated.getStartTime(), updated.getEndTime(), id)) {
            throw new IllegalArgumentException("Showtime overlaps with existing one in same theater!");
        }

        //existing.updateShowtimeDetails(existing);
        existing.setMovieId(updated.getMovieId());
        existing.setTheater(updated.getTheater());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setPrice(updated.getPrice());


        return showtimeRepository.save(existing);
    }

    @Override
    public void deleteShowtime(Long id) {
        showtimeRepository.deleteById(id);
    }

    private boolean isOverlapping(String theater, LocalDateTime start, LocalDateTime end, Long excludeId) {
        List<Showtime> showtimes = showtimeRepository.findByTheater(theater);
        for (Showtime s : showtimes) {
            if (excludeId != null && s.getId().equals(excludeId)) continue;
            if (!(end.isBefore(s.getStartTime()) || start.isAfter(s.getEndTime()))) {
                return true;
            }
        }
        return false;
    }
}
