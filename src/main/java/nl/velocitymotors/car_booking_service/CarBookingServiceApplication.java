package nl.velocitymotors.car_booking_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CarBookingServiceApplication {

	static void main(String[] args) {
		SpringApplication.run(CarBookingServiceApplication.class, args);
	}

}
