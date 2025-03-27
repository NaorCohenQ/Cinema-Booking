package com.att.tdp.popcorn_palace.ControllerTests;

import com.att.tdp.popcorn_palace.DTO.MovieRequest;
import com.att.tdp.popcorn_palace.Models.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;



    @Test
    void testRealClientPostRequest() {
        MovieRequest newMovie = new MovieRequest("AvatarAppTest", "Fantasy", 162, 8.0, 2009);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MovieRequest> request = new HttpEntity<>(newMovie, headers);

        ResponseEntity<Movie> response = restTemplate.postForEntity("http://localhost:" + port + "/movies", request, Movie.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("AvatarAppTest");
    }
}

