package nl.velocitymotors.car_booking_service.port.in;

import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingExecuted;

public interface ConfirmCarBookingPort {

    CarBookingExecuted execute(CarBookingConfirmCommand command);

}
