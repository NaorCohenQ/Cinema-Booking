package com.att.tdp.popcorn_palace.Conflicts;

public class ErrorMessages {

    // Movie validation
    public static final String MOVIE_TITLE_REQUIRED = "Movie title is required!";
    public static final String GENRE_REQUIRED = "Genre is required!";
    public static final String RATING_REQUIRED = "Rating is required!";
    public static final String DURATION_INVALID = "Duration cannot be positive!";

    // Showtime errors
    public static final String SHOWTIME_MOVIE_ID_REQUIRED = "Invalid Movie ID!";
    public static final String SHOWTIME_THEATER_REQUIRED = "Theater name is required!";
    public static final String SHOWTIME_START_TIME_REQUIRED = "Start time is required!";
    public static final String SHOWTIME_END_TIME_REQUIRED = "End time is required!";
    public static final String SHOWTIME_INVALID_PRICE = "Price must be greater than 0!";


    // Booking validation
    public static final String BOOKING_SHOWTIME_ID_REQUIRED = "Showtime ID is required!";
    public static final String BOOKING_SEAT_NUMBER_INVALID = "Seat number must be a positive number!";
    public static final String BOOKING_USER_ID_REQUIRED = "User ID is required!";
    public static final String BOOKING_USER_ID_INVALID = "Invalid UUID format for userId.";

    // Common messages
    public static final String INVALID_JSON = "Invalid JSON input.";
    public static final String UUID_INVALID = "Invalid UUID format for userId.";
    public static final String NUMBER_FORMAT_ERROR = "Invalid number format for field: ";
    public static final String TEXT_FORMAT_ERROR = "Invalid text input for field: ";
    public static final String LOW_DURATION = "Showtime duration is shorter than the movie duration";
}
