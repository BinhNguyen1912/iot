package com.nguyenanhbinh.lab306new;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Lab306newApplication {

	public static void main(String[] args) {
		System.out.println("SPRING_DATASOURCE_URL = " + System.getenv("SPRING_DATASOURCE_URL"));
		SpringApplication.run(Lab306newApplication.class, args);
	}

}
