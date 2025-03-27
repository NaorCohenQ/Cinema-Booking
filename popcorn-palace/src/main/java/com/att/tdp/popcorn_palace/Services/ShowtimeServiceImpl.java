package com.att.tdp.popcorn_palace.Services;
import com.att.tdp.popcorn_palace.Conflicts.ConflictException;
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
            logger.info("-- Loading all movies from DB...");
        return showtimeRepository.findAll();
    }

    @Override
    public Showtime getShowtimeById(Long showtimeID) {
        return getShowtime(showtimeID);

    }

    private Showtime getShowtime(Long showtimeID) {
        logger.info("Try get showtime with ID: "+showtimeID+" from the DB");
        return showtimeRepository.findById(showtimeID)
                .orElseThrow(() -> new EntityNotFoundException("Showtime not found"));
    }

    @Override
    public Showtime addShowtime(ShowtimeRequest showtimeDTO) {

        if (!movieServiceAPI.validateIfMovieExists(showtimeDTO.getMovieId())) {
            throw new EntityNotFoundException("Movie ID not found!");
        }

        showtimeValidation(showtimeDTO);
        validateOverlapping(showtimeDTO,null);

        Showtime addedShowtime = showtimeRepository.save(new Showtime(showtimeDTO));
        logger.info("✅ Showtime added: {}", addedShowtime.getId());
        return addedShowtime;
    }

private void showtimeValidation(ShowtimeRequest showtimeDTO) {
        LocalDateTime startTime = showtimeDTO.getStartTime();
        LocalDateTime endTime = showtimeDTO.getEndTime();
    if (!showtimeDTO.getEndTime().isAfter(showtimeDTO.getStartTime())) {
        throw new ConflictException("End time must be after start time.");
    }
    //Validate duration fits.
        int duration = movieServiceAPI.getMovieIfExists(showtimeDTO.getMovieId()).getDuration();
        long givenDurationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        if(duration > givenDurationMinutes){
            throw new ConflictException(ErrorMessages.LOW_DURATION +"(" + duration+ " minutes)");
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
                throw new ConflictException("The requested Showtime overlaps with Showtime with ID "+s.getId()+
                        "and both of them in the same theater: "+showtimeDTO.getTheater());
            }
        }
        return false;
    }

    @Override
    public void deleteAllByMovieId(Long movieID){
        showtimeRepository.deleteAllByMovieId(movieID);
    }
}
