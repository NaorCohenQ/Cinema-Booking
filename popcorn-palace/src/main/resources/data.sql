INSERT INTO movies (title, genre, duration, rating, release_year) VALUES
('Oppenheimer', 'Biography', 180, '8.6', 2023),
('Inception', 'Sci-Fi', 148, '9.0', 2010),
('The Dark Knight', 'Action', 152, '9.0', 2008),
('Interstellar', 'Sci-Fi', 169, '8.6', 2014),
('Titanic', 'Drama', 195, '7.8', 1997),
('Avatar', 'Sci-Fi', 162, '7.9', 2009),
('The Godfather', 'Crime', 175, '9.2', 1972),
('Pulp Fiction', 'Crime', 154, '8.9', 1994);

INSERT INTO showtimes (movie_id, theater, start_time, end_time, price) VALUES
(1, 'Theater 1', NOW() + INTERVAL '1 day', NOW() + INTERVAL '1 day' + INTERVAL '148 minutes', 45.0),
(2, 'Theater 1', NOW() + INTERVAL '1 day', NOW() + INTERVAL '1 day' + INTERVAL '148 minutes', 45.0),
(3, 'Theater 2', NOW() + INTERVAL '2 day', NOW() + INTERVAL '2 day' + INTERVAL '152 minutes', 50.0),
(4, 'Theater 3', NOW() + INTERVAL '3 day', NOW() + INTERVAL '3 day' + INTERVAL '169 minutes', 60.0),
(5, 'Theater 4', NOW() + INTERVAL '1 day', NOW() + INTERVAL '1 day' + INTERVAL '180 minutes', 70.0);

INSERT INTO bookings (booking_id, showtime_id, seat_number, user_id) VALUES
(gen_random_uuid(), 1, 5, gen_random_uuid()),
(gen_random_uuid(), 2, 10, gen_random_uuid());
