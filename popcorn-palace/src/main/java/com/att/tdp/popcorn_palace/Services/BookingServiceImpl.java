package com.att.tdp.popcorn_palace.Services;

import com.att.tdp.popcorn_palace.Conflicts.ConflictException;
import com.att.tdp.popcorn_palace.DTO.BookingRequest;
import com.att.tdp.popcorn_palace.Models.Booking;
import com.att.tdp.popcorn_palace.Repositories.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Logger;

@Service
public class BookingServiceImpl implements BookingServiceAPI {

    private static final Logger logger = Logger.getLogger(BookingServiceImpl.class.getName());

    private final BookingRepository bookingRepository;
    private final ShowtimeServiceAPI showtimeService;

    public BookingServiceImpl(BookingRepository bookingRepository, ShowtimeServiceAPI showtimeService) {
        this.bookingRepository = bookingRepository;
        this.showtimeService = showtimeService;
    }

    @Override
    public UUID createBooking(BookingRequest bookingDTO) {
        int seatNumber = bookingDTO.getSeatNumber();
        long showtimeID = bookingDTO.getShowtimeId();

        logger.info("--Try Creating booking for showtime ID " + showtimeID + ", seat " + seatNumber);

        // Validate showtime exists
        showtimeService.getShowtimeById(showtimeID);

        // Check if seat already booked
        validateSeat(seatNumber, showtimeID);

        Booking booking = new Booking(bookingDTO);
        bookingRepository.save(booking);

        logger.info("✅ Booking successfully created with ID: " + booking.getBookingId());
        return booking.getBookingId();
    }

    private void validateSeat(int seatNumber, long showtimeID) {
        if (bookingRepository.findByShowtimeIdAndSeatNumber(showtimeID, seatNumber).isPresent()) {
            String message = "Seat " + seatNumber + " is already booked for showtime ID " + showtimeID;
            logger.warning("!️ " + message);
            throw new ConflictException(message);
        }
    }

}
