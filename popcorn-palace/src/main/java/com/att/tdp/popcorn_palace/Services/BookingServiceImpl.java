package com.att.tdp.popcorn_palace.Services;

import com.att.tdp.popcorn_palace.Conflicts.SeatAlreadyBookedException;
import com.att.tdp.popcorn_palace.DTO.BookingRequest;
import com.att.tdp.popcorn_palace.Models.Booking;
import com.att.tdp.popcorn_palace.Repositories.BookingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

        logger.info("🎟️ Creating booking for showtime ID " + showtimeID + ", seat " + seatNumber);

        // Validate showtime exists
        showtimeService.getShowtimeById(showtimeID); // throws EntityNotFoundException if not found

        // Check if seat already booked
        if (bookingRepository.findByShowtimeIdAndSeatNumber(showtimeID, seatNumber).isPresent()) {
            String message = "Seat " + seatNumber + " is already booked for showtime ID " + showtimeID;
            logger.warning("⚠️ " + message);
            throw new SeatAlreadyBookedException(message); // <-- use 409
        }

        Booking booking = new Booking(bookingDTO);
        bookingRepository.save(booking);

        logger.info("✅ Booking confirmed with ID: " + booking.getBookingId());
        return booking.getBookingId();
    }
}
