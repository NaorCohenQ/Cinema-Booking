package com.att.tdp.popcorn_palace.Services;
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
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ShowtimeServiceImpl implements ShowtimeServiceAPI {

    private final ShowtimeRepository showtimeRepository;

    private static final Logger logger = LoggerFactory.getLogger(ShowtimeServiceImpl.class);


    private final ConcurrentHashMap<Long, Showtime> _allShowtimes = new ConcurrentHashMap<>();
    private boolean isAllShowtimesLoaded = false;
    //private final MovieRepository movieRepository;
    private final MovieServiceAPI movieServiceAPI;


    public ShowtimeServiceImpl(ShowtimeRepository showtimeRepository, MovieServiceAPI movieServiceAPI) {
        this.showtimeRepository = showtimeRepository;
        this.movieServiceAPI = movieServiceAPI;
    }

    @Override
    public List<Showtime> getAllShowtimes() {
        if(!isAllShowtimesLoaded){
            logger.info("⏬ Loading all movies from DB...");
            List<Showtime> allShowtimes = showtimeRepository.findAll();
            for(Showtime showtime : allShowtimes){
                if(!_allShowtimes.containsKey(showtime.getId()))
                    _allShowtimes.put(showtime.getId(),showtime);
            }
            isAllShowtimesLoaded = true;
        }
        return _allShowtimes.values().stream().toList();
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
        if(_allShowtimes.containsKey(showtimeID))
            return _allShowtimes.get(showtimeID);
        return showtimeRepository.findById(showtimeID)
                .orElseThrow(() -> new EntityNotFoundException("Showtime with id: "+showtimeID+" not found"));
    }

    @Override
    public Showtime addShowtime(ShowtimeRequest showtimeDTO) {
            showtimeValidation(showtimeDTO);

//        if (!movieServiceAPI.existsById(showtimeDTO.getMovieId())) {
//            throw new EntityNotFoundException("Movie ID not found!");


        if (!movieServiceAPI.validateIfMovieExists(showtimeDTO.getMovieId())) {
            throw new EntityNotFoundException("Movie ID not found!");
        }
            validateOverlapping(showtimeDTO,null);
//        if (isOverlapping(showtimeDTO, null)) {
//            throw new IllegalArgumentException("Showtime overlaps with existing one in same theater!");
//        }
        Showtime addedShowtime = showtimeRepository.save(new Showtime(showtimeDTO));
        _allShowtimes.put(addedShowtime.getId(),addedShowtime);
        return addedShowtime;
    }

    private void showtimeValidation(ShowtimeRequest showtimeDTO) {
        if (showtimeDTO.getMovieId() == null || showtimeDTO.getMovieId() <= 0) {
            throw new IllegalArgumentException("Invalid movie ID.");
        }

        if (showtimeDTO.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be a negative number.");
        }

        if (showtimeDTO.getTheater() == null || showtimeDTO.getTheater().trim().isEmpty()) {
            throw new IllegalArgumentException("Theater name cannot be empty.");
        }

        if (showtimeDTO.getStartTime() == null || showtimeDTO.getEndTime() == null) {
            throw new IllegalArgumentException("Start and end time must be provided.");
        }

        if (!showtimeDTO.getEndTime().isAfter(showtimeDTO.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }


    }

    @Override
    public Showtime updateShowtime(Long toUpdateID, ShowtimeRequest showtimeDTO) {

        showtimeValidation(showtimeDTO);
        Showtime toUpdateShowtime = getShowTime(toUpdateID);

        if (!movieServiceAPI.validateIfMovieExists(showtimeDTO.getMovieId())) {
            throw new EntityNotFoundException("Movie ID not found!");
        }

        validateOverlapping(showtimeDTO,toUpdateID);

        toUpdateShowtime.updateDetails(showtimeDTO);
        return showtimeRepository.save(toUpdateShowtime);
    }

    @Override
    @Transactional
    public void deleteShowtime(Long id) {
        if(isAllShowtimesLoaded && !_allShowtimes.containsKey(id))
            throw new EntityNotFoundException("There is no such showtime with ID : "+id+" Therefore, a request to delete it cannot be done.");
          showtimeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Showtime not found"));
        showtimeRepository.deleteById(id);
        if(_allShowtimes.containsKey(id))
            _allShowtimes.remove(id);
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
}
