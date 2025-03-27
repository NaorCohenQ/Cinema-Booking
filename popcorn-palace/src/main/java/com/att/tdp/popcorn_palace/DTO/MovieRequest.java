package com.att.tdp.popcorn_palace.DTO;


import com.att.tdp.popcorn_palace.Conflicts.ErrorMessages;
import jakarta.validation.constraints.*;

public class MovieRequest {

    private Long id; // Optional, typically unused in POST requests


    @NotBlank(message = ErrorMessages.MOVIE_TITLE_REQUIRED)
    private String title;

    @NotBlank(message = ErrorMessages.GENRE_REQUIRED)
    private String genre;

    @Min(value = 0, message = ErrorMessages.DURATION_INVALID)
    private int duration;
    @DecimalMin(value = "0.0", inclusive = true, message = ErrorMessages.RATING_REQUIRED)
    private double rating;

//    @Min(value = 1, message = ErrorMessages.DURATION_INVALID)
    private int releaseYear;

    public MovieRequest() {}

    public MovieRequest(String title, String genre, int duration, double rating, int releaseYear) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.releaseYear = releaseYear;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getDuration() { return duration; }
    public double getRating() { return rating; }
    public int getReleaseYear() { return releaseYear; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setRating(double rating) { this.rating = rating; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
}
