package nl.velocitymotors.car_booking_service.domain.model;

import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidCarConfirmationException;

import java.time.Duration;
import java.time.OffsetDateTime;

public record RentalPeriod(OffsetDateTime start, OffsetDateTime end) {

    private static final int MAX_RENTAL_DAYS = 21;
    private static final Duration MAX_RENTAL_DURATION = Duration.ofDays(MAX_RENTAL_DAYS);

    public RentalPeriod {
        if (start == null || end == null) {
            throw new InvalidCarConfirmationException("The rental start and end dates are required");
        }
        if (start.isAfter(end)) {
            throw new InvalidCarConfirmationException("The car booking start date cannot be greater than the end date");
        }
        if (Duration.between(start, end).compareTo(MAX_RENTAL_DURATION) > 0) {
            throw new InvalidCarConfirmationException("The maximum number of days for a car booking is " + MAX_RENTAL_DAYS);
        }
    }

}
