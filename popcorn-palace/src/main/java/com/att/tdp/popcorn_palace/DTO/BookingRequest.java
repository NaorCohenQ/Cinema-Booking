package com.att.tdp.popcorn_palace.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class BookingRequest {

    @NotNull(message = "Showtime ID is required")
    private Long showtimeId;

    @NotNull(message = "Seat number is required")
    @Min(value = 1, message = "Seat number must be greater than 0")
    private Integer seatNumber;

    @NotNull(message = "User ID is required")
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
