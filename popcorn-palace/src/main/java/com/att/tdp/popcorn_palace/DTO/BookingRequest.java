package com.att.tdp.popcorn_palace.DTO;

import com.att.tdp.popcorn_palace.Conflicts.ErrorMessages;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class BookingRequest {

    @NotNull(message = ErrorMessages.BOOKING_SHOWTIME_ID_REQUIRED)
    private Long showtimeId;

    @NotNull(message = ErrorMessages.BOOKING_SEAT_NUMBER_INVALID)
    @Min(value = 1, message = ErrorMessages.BOOKING_SEAT_NUMBER_INVALID)
    private Integer seatNumber;

    @NotNull(message = ErrorMessages.BOOKING_USER_ID_INVALID)
    private UUID userId;

    public BookingRequest() {
    }

    public BookingRequest(Long showtimeId, int seatNumber, UUID userId) {
        this.showtimeId = showtimeId;
        this.seatNumber = seatNumber;
        this.userId = userId;
    }

    public Long getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Long showtimeId) {
        this.showtimeId = showtimeId;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
