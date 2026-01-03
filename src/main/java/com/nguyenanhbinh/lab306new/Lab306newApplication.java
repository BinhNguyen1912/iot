package com.nguyenanhbinh.lab306new;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Lab306newApplication {

	public static void main(String[] args) {
		System.out.println("DATABASE_URL = " + System.getenv("DATABASE_URL"));
		SpringApplication.run(Lab306newApplication.class, args);
	}

}
