package nl.velocitymotors.car_booking_service.usecases.validators;

import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidCarConfirmationException;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
public class CarBookingConfirmHasValidNumberOfDaysValidator implements CarBookingConfirmValidator {

    private static final int MAX_NUMBER_OF_DAYS_FOR_CAR_BOOKING_ALLOWED = 21;

    public void validate(CarBookingConfirmCommand request){
        long carBookingDays = ChronoUnit.DAYS.between(request.rentalStartDate(), request.rentalEndDate());
        if(carBookingDays > MAX_NUMBER_OF_DAYS_FOR_CAR_BOOKING_ALLOWED){
            throw new InvalidCarConfirmationException("The maximum number of days for a car booking is " + MAX_NUMBER_OF_DAYS_FOR_CAR_BOOKING_ALLOWED );
        }
    }
}
