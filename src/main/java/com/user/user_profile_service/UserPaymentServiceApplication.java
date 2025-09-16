package com.user.user_profile_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "com.user.user_profile_service.repository.repo")
public class UserPaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserPaymentServiceApplication.class, args);
	}

}
