INSERT INTO movies (title, genre, duration, rating, release_year) VALUES
('The Meg', 'Action', 113, 5.7, 2018),
('Shutter Island', 'Mystery', 138, 8.2, 2010),
('Gladiator', 'Action', 155, 8.5, 2000),
('Oppenheimer', 'Biography', 180, 8.6, 2023),
('Avatar', 'Sci-Fi', 162, 7.9, 2009);


INSERT INTO showtimes (movie_id, theater, start_time, end_time, price) VALUES
(1, 'Theater 1', NOW() + INTERVAL '1 day 10:00', NOW() + INTERVAL '1 day 11:53', 40.0),
(2, 'Theater 1', NOW() + INTERVAL '1 day 12:00', NOW() + INTERVAL '1 day 14:18', 45.0),
(3, 'Theater 2', NOW() + INTERVAL '2 day 10:00', NOW() + INTERVAL '2 day 12:35', 50.0),
(4, 'Theater 3', NOW() + INTERVAL '3 day 15:00', NOW() + INTERVAL '3 day 18:00', 60.0);



INSERT INTO bookings (booking_id, showtime_id, seat_number, user_id) VALUES
(gen_random_uuid(), 1, 5, gen_random_uuid()),
(gen_random_uuid(), 2, 10, gen_random_uuid());
