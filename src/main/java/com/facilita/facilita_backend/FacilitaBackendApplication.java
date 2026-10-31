package com.facilita.facilita_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.facilita.facilita_backend.model")
public class FacilitaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FacilitaBackendApplication.class, args);
	}

}
