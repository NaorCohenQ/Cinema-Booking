package com.att.tdp.popcorn_palace.Controllers;

import com.att.tdp.popcorn_palace.DTO.BookingRequest;
import com.att.tdp.popcorn_palace.Services.BookingServiceAPI;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.Map;


@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingServiceAPI bookingService;

    public BookingController(BookingServiceAPI bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody @Valid BookingRequest request) {
        UUID bookingId = bookingService.createBooking(request);
        return ResponseEntity.ok(Map.of("bookingId", bookingId));
    }
}
