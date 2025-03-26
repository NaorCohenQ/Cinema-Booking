package com.att.tdp.popcorn_palace.Services;
import com.att.tdp.popcorn_palace.Conflicts.ErrorMessages;
import com.att.tdp.popcorn_palace.DTO.ShowtimeRequest;
import com.att.tdp.popcorn_palace.Models.Showtime;
import com.att.tdp.popcorn_palace.Repositories.ShowtimeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShowtimeServiceImpl implements ShowtimeServiceAPI {

    private final ShowtimeRepository showtimeRepository;

    private static final Logger logger = LoggerFactory.getLogger(ShowtimeServiceImpl.class);


    private final MovieServiceAPI movieServiceAPI;


    public ShowtimeServiceImpl(ShowtimeRepository showtimeRepository, MovieServiceAPI movieServiceAPI) {
        this.showtimeRepository = showtimeRepository;
        this.movieServiceAPI = movieServiceAPI;
    }

    @Override
    public List<Showtime> getAllShowtimes() {
            logger.info("⏬ Loading all movies from DB...");
        return showtimeRepository.findAll();
    }

    @Override
    public Showtime getShowtimeById(Long showtimeID) {
        return getShowtime(showtimeID);

    }

    private Showtime getShowtime(Long showtimeID) {
        logger.info("Try get showtime with ID: "+showtimeID+"from the DB");
        return showtimeRepository.findById(showtimeID)
                .orElseThrow(() -> new EntityNotFoundException("Showtime not found"));
    }

//    @Override
//    public Showtime getShowtimeById(Long showtimeID) {
//            return getShowTime(showtimeID);
//
//    }

//    private Showtime getShowTime(Long showtimeID) {
//        if(_allShowtimes.containsKey(showtimeID))
//            return _allShowtimes.get(showtimeID);
//        return showtimeRepository.findById(showtimeID)
//                .orElseThrow(() -> new EntityNotFoundException("Showtime with id: "+showtimeID+" not found"));
//    }

    @Override
    public Showtime addShowtime(ShowtimeRequest showtimeDTO) {
        //showtimeValidation(showtimeDTO);

        if (!movieServiceAPI.validateIfMovieExists(showtimeDTO.getMovieId())) {
            throw new EntityNotFoundException("Movie ID not found!");
        }

        showtimeValidation(showtimeDTO);
        validateOverlapping(showtimeDTO,null);

        Showtime addedShowtime = showtimeRepository.save(new Showtime(showtimeDTO));
        logger.info("✅ Showtime added: {}", addedShowtime.getId());
        return addedShowtime;
    }

//    private void showtimeValidation(ShowtimeRequest showtimeDTO) {
//        if (showtimeDTO.getMovieId() == null || showtimeDTO.getMovieId() <= 0) {
//            throw new IllegalArgumentException("Invalid movie ID.");
//        }
//
//        if (showtimeDTO.getPrice() < 0) {
//            throw new IllegalArgumentException("Price cannot be a negative number.");
//        }
//
//        if (showtimeDTO.getTheater() == null || showtimeDTO.getTheater().trim().isEmpty()) {
//            throw new IllegalArgumentException("Theater name cannot be empty.");
//        }
//
//        if (showtimeDTO.getStartTime() == null || showtimeDTO.getEndTime() == null) {
//            throw new IllegalArgumentException("Start and end time must be provided.");
//        }
//
//        if (!showtimeDTO.getEndTime().isAfter(showtimeDTO.getStartTime())) {
//            throw new IllegalArgumentException("End time must be after start time.");
//        }
//
//
//    }
private void showtimeValidation(ShowtimeRequest showtimeDTO) {
        LocalDateTime startTime = showtimeDTO.getStartTime();
        LocalDateTime endTime = showtimeDTO.getEndTime();
    if (!showtimeDTO.getEndTime().isAfter(showtimeDTO.getStartTime())) {
        throw new IllegalArgumentException("End time must be after start time.");
    }
    //Validate duration fits.
        int duration = movieServiceAPI.getMovieIfExists(showtimeDTO.getMovieId()).getDuration();
        long givenDurationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        if(duration > givenDurationMinutes){
            throw new IllegalArgumentException(ErrorMessages.LOW_DURATION +"(" + duration+ " minutes)");
        }
}

    @Override
    public Showtime updateShowtime(Long toUpdateID, ShowtimeRequest showtimeDTO) {


        Showtime toUpdateShowtime = getShowtime(toUpdateID);

        if (!movieServiceAPI.validateIfMovieExists(showtimeDTO.getMovieId())) {
            throw new EntityNotFoundException("Movie ID not found!");
        }
        showtimeValidation(showtimeDTO);
        validateOverlapping(showtimeDTO,toUpdateID);

        toUpdateShowtime.updateDetails(showtimeDTO);
        Showtime updatedShowtime = showtimeRepository.save(toUpdateShowtime);
        logger.info("✅ Showtime Updated: {}", updatedShowtime.getId());
        return showtimeRepository.save(toUpdateShowtime);
    }


    @Override
    @Transactional
    public void deleteShowtime(Long id) {
        showtimeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Showtime with ID " + id + " not found!"));
        showtimeRepository.deleteById(id);
        logger.info("🗑️ Showtime deleted with ID {}", id);
    }



    private boolean validateOverlapping(ShowtimeRequest showtimeDTO , Long excludeId) {
        LocalDateTime start= showtimeDTO.getStartTime();
        LocalDateTime end= showtimeDTO.getEndTime();
        List<Showtime> showtimes = showtimeRepository.findByTheater(showtimeDTO.getTheater());
        for (Showtime s : showtimes) {
            if (excludeId != null && s.getId().equals(excludeId)) continue;
            if (!(end.isBefore(s.getStartTime()) || start.isAfter(s.getEndTime()))) {
                throw new IllegalArgumentException("The requested Showtime overlaps with Showtime with ID"+s.getId()+
                        "and both of the in the same theater :"+showtimeDTO.getTheater());
            }
        }
        return false;
    }

    @Override
    public void deleteAllByMovieId(Long movieID){
        showtimeRepository.deleteAllByMovieId(movieID);

    }
}
