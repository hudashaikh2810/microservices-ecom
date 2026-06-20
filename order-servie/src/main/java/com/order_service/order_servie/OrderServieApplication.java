package com.order_service.order_servie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrderServieApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServieApplication.class, args);
	}

}
