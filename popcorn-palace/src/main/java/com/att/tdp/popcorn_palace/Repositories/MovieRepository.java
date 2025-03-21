package com.att.tdp.popcorn_palace.Repositories;

import com.att.tdp.popcorn_palace.Models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTitle(String title);
    void deleteByTitle(String title);
}
