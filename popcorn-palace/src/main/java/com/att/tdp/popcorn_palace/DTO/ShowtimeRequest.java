package com.att.tdp.popcorn_palace.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

public class ShowtimeRequest {
        private Long id;

        private Long movieId;
        private String theater;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private double price;

        public ShowtimeRequest() {}

        public ShowtimeRequest(Long movieId, String theater, LocalDateTime startTime, LocalDateTime endTime, double price) {
            this.movieId = movieId;
            this.theater = theater;
            this.startTime = startTime;
            this.endTime = endTime;
            this.price = price;
        }

        public Long getId() { return id; }
        public Long getMovieId() { return movieId; }
        public void setMovieId(Long movieId) { this.movieId = movieId; }

        public String getTheater() { return theater; }
        public void setTheater(String theater) { this.theater = theater; }

        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
}
