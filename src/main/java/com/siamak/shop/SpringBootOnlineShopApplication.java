package com.siamak.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBootOnlineShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootOnlineShopApplication.class, args);
	}

}


