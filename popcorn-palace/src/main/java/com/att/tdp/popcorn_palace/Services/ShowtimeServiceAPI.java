package com.att.tdp.popcorn_palace.Services;
import com.att.tdp.popcorn_palace.Models.Showtime;

import java.util.List;

public interface ShowtimeServiceAPI {
    List<Showtime> getAllShowtimes();
    Showtime getShowtimeById(Long id);
    Showtime addShowtime(Showtime showtime);
    Showtime updateShowtime(Long id, Showtime showtime);
    void deleteShowtime(Long id);
}
