package nl.velocitymotors.car_booking_service.port.in;

import domain.model.CarBookingConfirmCommand;
import domain.model.CarBookingExecuted;

public interface ConfirmCarBookingPort {

    CarBookingExecuted execute(CarBookingConfirmCommand command);

}
