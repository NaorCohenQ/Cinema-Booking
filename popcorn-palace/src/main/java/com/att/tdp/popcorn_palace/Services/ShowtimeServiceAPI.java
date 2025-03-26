package com.att.tdp.popcorn_palace.Services;
import com.att.tdp.popcorn_palace.DTO.ShowtimeRequest;
import com.att.tdp.popcorn_palace.Models.Showtime;

import java.util.List;

public interface ShowtimeServiceAPI {
    List<Showtime> getAllShowtimes();
    Showtime getShowtimeById(Long id);
    Showtime addShowtime(ShowtimeRequest showtime);
    Showtime updateShowtime(Long id, ShowtimeRequest showtime);
    void deleteShowtime(Long id);
    void deleteAllByMovieId(Long movieID);
}
