package com.tashin.physicsai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PhysicsaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhysicsaiApplication.class, args);
	}

}
