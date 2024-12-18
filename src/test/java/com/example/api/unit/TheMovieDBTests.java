package com.example.api.unit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
 class TheMovieDBTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	 void homeResponse() {
		String body = this.restTemplate.getForObject("/", String.class);
		assertThat(body).isEqualTo("Spring is here!");
	}
}
