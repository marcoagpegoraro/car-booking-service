package nl.velocitymotors.car_booking_service.usecases.validators;

import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidCarConfirmationException;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import org.springframework.stereotype.Component;

@Component
public class CarBookingConfirmVehicleIDValidator implements CarBookingConfirmValidator {

    public void validate(CarBookingConfirmCommand request){
        if(request.vehicleID().isBlank()){
            throw new InvalidCarConfirmationException("The vehicle ID cannot be empty.");
        }
    }
}
