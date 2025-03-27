package com.att.tdp.popcorn_palace.Models;

import com.att.tdp.popcorn_palace.DTO.MovieRequest;
import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "movies")
@Getter
@Setter
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String genre;
    private int duration;
    private double rating;
    private int releaseYear;

    public Movie() {}

    public Movie(String title, String genre, int duration, double rating, int releaseYear) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.releaseYear = releaseYear;
    }

    public Movie(MovieRequest movieDTO) {
        this.title = movieDTO.getTitle();
        this.genre = movieDTO.getGenre();
        this.duration = movieDTO.getDuration();
        this.rating = movieDTO.getRating();
        this.releaseYear = movieDTO.getReleaseYear();
    }
    public void updateDetails(MovieRequest movieDTO) {
        this.title = movieDTO.getTitle();
        this.genre = movieDTO.getGenre();
        this.duration = movieDTO.getDuration();
        this.rating = movieDTO.getRating();
        this.releaseYear = movieDTO.getReleaseYear();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getDuration() { return duration; }
    public double getRating() { return rating; }
    public int getReleaseYear() { return releaseYear; }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }
}
