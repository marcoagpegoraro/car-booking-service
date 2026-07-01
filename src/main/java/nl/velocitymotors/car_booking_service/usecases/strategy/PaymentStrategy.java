package nl.velocitymotors.car_booking_service.usecases.strategy;

import nl.velocitymotors.car_booking_service.domain.model.Booking;

public interface PaymentStrategy {
    void apply(Booking booking);
}
