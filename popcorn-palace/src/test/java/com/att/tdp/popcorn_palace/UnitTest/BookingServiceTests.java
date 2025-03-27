package com.att.tdp.popcorn_palace.UnitTest;

import com.att.tdp.popcorn_palace.DTO.BookingRequest;
import com.att.tdp.popcorn_palace.Models.Booking;
import com.att.tdp.popcorn_palace.Repositories.BookingRepository;
import com.att.tdp.popcorn_palace.Services.BookingServiceImpl;
import com.att.tdp.popcorn_palace.Services.ShowtimeServiceAPI;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTests {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ShowtimeServiceAPI showtimeService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        bookingRequest = new BookingRequest(1L, 5, UUID.randomUUID());
    }

    @Test
    void createBooking_shouldSucceed_whenSeatIsAvailable() {
        when(showtimeService.getShowtimeById(1L)).thenReturn(null); // just for validation
        when(bookingRepository.findByShowtimeIdAndSeatNumber(1L, 5)).thenReturn(Optional.empty());

        // simulate save returns same booking with a generated ID
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UUID bookingId = bookingService.createBooking(bookingRequest);

        assertThat(bookingId).isNotNull();
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowException_whenSeatAlreadyBooked() {
        when(showtimeService.getShowtimeById(1L)).thenReturn(null);
        when(bookingRepository.findByShowtimeIdAndSeatNumber(1L, 5))
                .thenReturn(Optional.of(new Booking(bookingRequest)));

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Seat 5 is already booked");
    }

    @Test
    void createBooking_shouldThrowException_whenShowtimeDoesNotExist() {
        // This simulates the showtime lookup throwing EntityNotFoundException
        when(showtimeService.getShowtimeById(1L)).thenThrow(new EntityNotFoundException("Showtime not found"));

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Showtime not found");
    }

    @Test
    void createBooking_shouldSaveBookingWithCorrectDetails() {
        when(showtimeService.getShowtimeById(1L)).thenReturn(null);
        when(bookingRepository.findByShowtimeIdAndSeatNumber(1L, 5)).thenReturn(Optional.empty());

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            return booking;
        });

        UUID bookingId = bookingService.createBooking(bookingRequest);

        assertThat(bookingId).isNotNull();
        verify(bookingRepository).save(argThat(b ->
                b.getSeatNumber() == 5 &&
                        b.getShowtimeId() == 1L &&
                        b.getUserId().equals(bookingRequest.getUserId())
        ));
    }
}
