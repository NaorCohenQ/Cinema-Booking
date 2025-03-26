package com.att.tdp.popcorn_palace.Controllers;

import com.att.tdp.popcorn_palace.DTO.ShowtimeRequest;
import com.att.tdp.popcorn_palace.Models.Showtime;
import com.att.tdp.popcorn_palace.Services.ShowtimeServiceAPI;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    private final ShowtimeServiceAPI showtimeService;

    public ShowtimeController(ShowtimeServiceAPI showtimeService) {
        this.showtimeService = showtimeService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Showtime>> getAllShowtimes() {
        return ResponseEntity.ok(showtimeService.getAllShowtimes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Showtime> getShowtimeById(@PathVariable Long id) {

        return ResponseEntity.ok(showtimeService.getShowtimeById(id));
    }

    @PostMapping
    public ResponseEntity<Showtime> addShowtime(@RequestBody @Valid ShowtimeRequest showtimeDTO) {
        return ResponseEntity.ok(showtimeService.addShowtime(showtimeDTO));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Showtime> updateShowtime(@PathVariable Long id,  @RequestBody @Valid ShowtimeRequest showtimeDTO) {
        return ResponseEntity.ok(showtimeService.updateShowtime(id, showtimeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.ok().build();
    }
}
