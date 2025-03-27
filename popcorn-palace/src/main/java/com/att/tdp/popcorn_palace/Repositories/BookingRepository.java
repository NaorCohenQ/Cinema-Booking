package com.att.tdp.popcorn_palace.Repositories;

import com.att.tdp.popcorn_palace.Models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Optional<Booking> findByShowtimeIdAndSeatNumber(Long showtimeId, int seatNumber);
}
