package com.att.tdp.popcorn_palace.Services;

import com.att.tdp.popcorn_palace.DTO.BookingRequest;
import java.util.UUID;

public interface BookingServiceAPI {
    UUID createBooking(BookingRequest request);
    //void deleteAllByShowtimeId(Long showtimeID);
}
