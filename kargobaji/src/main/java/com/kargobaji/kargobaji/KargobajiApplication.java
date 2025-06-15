package com.kargobaji.kargobaji;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KargobajiApplication {
	public static void main(String[] args) {
		SpringApplication.run(KargobajiApplication.class, args);
	}
}
