package nl.velocitymotors.car_booking_service.usecases.validators;

import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidCarConfirmationException;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
public class CarBookingConfirmStartAndEndDateValidator implements CarBookingConfirmValidator {

    public void validate(CarBookingConfirmCommand request){
        if(request.rentalStartDate().isAfter(request.rentalEndDate())){
            throw new InvalidCarConfirmationException("The car booking start date cannot be greater than the end date.");
        }
    }
}
