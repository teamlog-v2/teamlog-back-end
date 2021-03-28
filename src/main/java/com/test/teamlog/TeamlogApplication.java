package com.test.teamlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TeamlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamlogApplication.class, args);
	}

}
