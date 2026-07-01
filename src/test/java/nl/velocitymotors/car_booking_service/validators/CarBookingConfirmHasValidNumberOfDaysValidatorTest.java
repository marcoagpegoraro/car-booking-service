package nl.velocitymotors.car_booking_service.validators;

import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.enums.VehicleCategoryEnum;
import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidCarConfirmationException;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.usecases.validators.CarBookingConfirmHasValidNumberOfDaysValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;


class CarBookingConfirmHasValidNumberOfDaysValidatorTest {

    private CarBookingConfirmHasValidNumberOfDaysValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CarBookingConfirmHasValidNumberOfDaysValidator();
    }

    @Test
    void validate_shouldPass_whenBookingDaysLessThanMax() {
        // GIVEN
        CarBookingConfirmCommand command = new CarBookingConfirmCommand("", "1",
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(10),
                VehicleCategoryEnum.LUXURY, PaymentModeEnum.CASH, ""
        );

        // WHEN / THEN
        assertDoesNotThrow(() -> validator.validate(command));
    }

    @Test
    void validate_shouldPass_whenBookingDaysExactlyAtMaxLimit() {
        // GIVEN
        CarBookingConfirmCommand command = new CarBookingConfirmCommand("", "1",
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(21),
                VehicleCategoryEnum.LUXURY, PaymentModeEnum.CASH, ""
        );

        // WHEN / THEN
        assertDoesNotThrow(() -> validator.validate(command));
    }

    @Test
    void validate_shouldThrowException_whenBookingDaysExceedMaxLimit() {
        // GIVEN

        CarBookingConfirmCommand command = new CarBookingConfirmCommand("", "1",
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(31),
                VehicleCategoryEnum.LUXURY, PaymentModeEnum.CASH, ""
        );

        // WHEN / THEN
        InvalidCarConfirmationException exception =
                assertThrows(
                        InvalidCarConfirmationException.class,
                        () -> validator.validate(command)
                );

        assertEquals(
                "The maximum number of days for a car booking is 21.",
                exception.getMessage()
        );
    }
}