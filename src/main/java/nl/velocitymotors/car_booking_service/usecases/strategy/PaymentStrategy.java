package nl.velocitymotors.car_booking_service.usecases.strategy;

import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingExecuted;

public interface PaymentStrategy {
    CarBookingExecuted execute(CarBookingConfirmCommand command);
}
