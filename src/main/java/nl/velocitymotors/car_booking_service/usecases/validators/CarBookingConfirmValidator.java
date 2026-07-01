package nl.velocitymotors.car_booking_service.usecases.validators;

import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;

public interface CarBookingConfirmValidator {

    void validate(CarBookingConfirmCommand request);
}
