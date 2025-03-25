package com.att.tdp.popcorn_palace.Models;
import com.att.tdp.popcorn_palace.DTO.BookingRequest;
import com.att.tdp.popcorn_palace.Repositories.BookingRepository;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "bookings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"showtimeId", "seatNumber"})
})
public class Booking {

    @Id
    private UUID bookingId;

    private Long showtimeId;
    private int seatNumber;
    private UUID userId;

    public Booking() {
        this.bookingId = UUID.randomUUID();
    }

    public Booking(Long showtimeId, int seatNumber, UUID userId) {
        this.bookingId = UUID.randomUUID();
        this.showtimeId = showtimeId;
        this.seatNumber = seatNumber;
        this.userId = userId;
    }

    public Booking(BookingRequest bookingDTO) {
        this.bookingId = UUID.randomUUID();
        this.showtimeId = bookingDTO.getShowtimeId();
        this.seatNumber = bookingDTO.getSeatNumber();
        this.userId = bookingDTO.getUserId();
    }

    public UUID getBookingId() {
        return bookingId;
    }

//    public void setBookingId(UUID bookingId) {
//        this.bookingId = bookingId;
//    }

    public Long getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Long showtimeId) {
        this.showtimeId = showtimeId;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
