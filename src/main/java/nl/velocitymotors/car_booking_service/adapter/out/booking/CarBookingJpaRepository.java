package nl.velocitymotors.car_booking_service.adapter.out.booking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CarBookingJpaRepository extends JpaRepository<CarBookingJpaEntity, String> {
}