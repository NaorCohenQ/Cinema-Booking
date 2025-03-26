package com.att.tdp.popcorn_palace.Repositories;

import com.att.tdp.popcorn_palace.Models.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByTheater(String theater);
    void deleteAllByMovieId(Long movieId);

}

