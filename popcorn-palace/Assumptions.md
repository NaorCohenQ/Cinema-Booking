# 📌 Assumptions & Development Information

This document outlines the key assumptions, design decisions, and deviations made during the development of the Popcorn Palace Movie Ticket Booking System.

---

## 1. RESTful API Deviations

While the system adheres mostly to RESTful principles, there are some intentional deviations due to the format provided in the original `README.md`:

- **POST** returns `200 OK` on successful creation. However, the REST standard recommends `201 Created`.
- **POST** is used for updating movies and showtimes. Conventionally, it should be **PUT** or **PATCH**.
- Similar deviation exists for updating showtimes.

I've decided to do so in order follow the exact assignment specification, but in a production-grade system, REST conventions should be followed more strictly.


## 2. Input Validation Assumptions

To maintain data integrity, the following checks were implemented even though not explicitly required:

- **Duration**, **Price**, and **Rating** must be valid:
  - `duration,price,rating>= 0`
- **`title`**, **`genre`**, and **`rating`** fields must not be blank.  
  - Validated using `@NotBlank` annotations.

- All IDs (e.g., `movieId`, `showtimeId`) must exist before referencing them.  
  - The system checks for existence before performing operations to avoid dangling references.



- **`seatNumber`** must be a positive integer.
  - Booking seat number must be ≥ 1.
  - No upper limit was enforced, but could be set per theater in future versions.

### Time Constraints
- **Showtime start and end time**:
  - `endTime` must be **after** `startTime`.
  - The time between them must be **at least the movie’s duration**.
    - This ensures that a movie isn't scheduled to end before it finishes.
    - Longer durations are allowed to support real-world scenarios like movie breaks or ads.

These assumptions and validations ensure that only logically correct and complete data can be submitted to the system, avoiding runtime errors or inconsistent state in the database.

---
## 3.Error Handling

Error cases were not defined in the original task instructions. Therefore, I introduced a **GlobalExceptionHandler** mechanism to manage them gracefully.
The system returns appropriate HTTP status codes depending on the nature of the error:

#### `400 BAD REQUEST`
This response is returned when the client sends invalid or logically incorrect input. Typical causes include:
- Missing or malformed fields
- Violations of validation constraints (e.g., duration less than 0)

These errors are typically handled via `IllegalArgumentException` or Spring’s built-in validation mechanism using annotations like `@Valid`, `@Min`, etc.

#### `404 NOT FOUND`
Returned when the requested resource (Movie, Showtime, Booking) is not found in the database.
This is handled by throwing `EntityNotFoundException`, either manually or via repository lookups that fail.

#### `409 CONFLICT`
This error is returned when the request causes a conflict with existing data.
- `409 CONFLICT` is used instead of `400` to indicate that the input is valid but violates a business rule.
Common situations include:
- Adding a movie that already exists
- Booking a seat that is already taken for the same showtime
- Creating an overlapping showtime in the same theater

A `ConflictException` is thrown in such cases to represent business rule violations.

---

### 4. Theater Service (Future Consideration)
Currently, **theater names** are just strings. If the project were to grow, a dedicated `TheaterService` could be introduced with:

- Theater entities stored in the DB
- Max seat capacity per theater
---

## 5. Delete Behavior & Consequences

The original specification required the ability to delete Movies and Showtimes.  
However, it might be problematic, as we might:
- Unintentionally deleting **upcoming Showtimes** and **future Bookings**
- Removing information that may still be relevant for customers or staff

In our system I followed the requirement to delete them, but in real-time development I would suggest a different approaches.

### Recommended Alternatives

**Option 1: Soft Deletion (Recommended)**
- Add a `deleted` flag to entities like `Movie` and `Showtime`.
- Filter out "deleted" items from API responses.
- This approach preserves history while ensuring deleted content doesn't appear in active workflows.

**Option 2: Conditional Hard Deletion**
- Allow deletion only if the movie/showtime has **no future bookings** or **no showtimes scheduled**.
- Could work well if we have no intention of keeping historical data(Bookings,Showtimes ,etc ..)

-----

## 6. Testing Summary

The system contains different tests.
- **Unit Tests**: Covered core logic in services (Movie, Showtime, Booking) to ensure methods behave correctly.
- **Controller Tests**: Used `MockMvc` to validate all endpoints with both valid and invalid input (missing fields, wrong data types, duplicates, conflicts). Verified proper HTTP status codes and messages.
- **Scenario Tests**: Simulated full flows across modules.

## 7. Additional API Endpoints Added

Although not required in the original specification, I added the following endpoints to enhance usability and API completeness:

- `GET /movies/{title}`  
  → Retrieve a movie by its title.  
  Useful for fetching individual movie details without needing to retrieve all movies and filter manually.

- `GET /showtimes/all`  
  → Retrieve a list of all available showtimes.  
  Complements the `GET /showtimes/{id}` endpoint and improves client-side flexibility.

These endpoints align with RESTful principles and offer better accessibility for clients needing more specific or complete dataset access.

----

## Summary
This document serves to explain the assumptions that has been made during the development, so as additional information that was important to note.