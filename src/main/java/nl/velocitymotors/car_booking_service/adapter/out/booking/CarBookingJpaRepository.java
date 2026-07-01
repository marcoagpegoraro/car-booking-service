package nl.velocitymotors.car_booking_service.adapter.out.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface CarBookingJpaRepository extends JpaRepository<CarBookingJpaEntity, String> {
    List<CarBookingJpaEntity> findByPaymentModeAndBookingStatusAndRentalStartDateLessThanEqual(
            String paymentMode,
            String bookingStatus,
            OffsetDateTime rentalStartDateThreshold);
}
