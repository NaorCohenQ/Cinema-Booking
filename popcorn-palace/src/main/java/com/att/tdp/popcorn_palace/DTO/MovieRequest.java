package com.att.tdp.popcorn_palace.DTO;

public class MovieRequest {

        private Long id;

        private String title;
        private String genre;
        private int duration;
        private String rating;
        private int releaseYear;

        public MovieRequest() {}

        public MovieRequest(String title, String genre, int duration, String rating, int releaseYear) {
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
        public String getRating() { return rating; }
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

        public void setRating(String rating) {
            this.rating = rating;
        }

        public void setReleaseYear(int releaseYear) {
            this.releaseYear = releaseYear;
        }
    }
