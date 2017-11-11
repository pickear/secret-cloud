package com.weasel.secret.cloud;

import com.weasel.secret.common.domain.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class SecretCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecretCloudApplication.class, args);
	}
}
