package com.weasel.secret.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = {"com.weasel.secret.common.domain"})
@SpringBootApplication
public class SecretCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecretCloudApplication.class, args);
	}
}
