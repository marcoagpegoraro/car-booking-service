package nl.velocitymotors.car_booking_service.validators;

import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.enums.VehicleCategoryEnum;
import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidCarConfirmationException;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.usecases.validators.CarBookingConfirmStartAndEndDateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CarBookingConfirmStartAndEndDateValidatorTest {

    private CarBookingConfirmStartAndEndDateValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CarBookingConfirmStartAndEndDateValidator();
    }

    @Test
    void validate_shouldPass_whenStartDateIsBeforeEndDate() {
        // GIVEN
        CarBookingConfirmCommand command = new CarBookingConfirmCommand("", "1",
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(5),
                VehicleCategoryEnum.LUXURY, PaymentModeEnum.CASH, ""
        );

        // WHEN / THEN
        assertDoesNotThrow(() -> validator.validate(command));
    }

    @Test
    void validate_shouldPass_whenStartDateEqualsEndDate() {
        // GIVEN
        OffsetDateTime sameDate = OffsetDateTime.now();
        CarBookingConfirmCommand command = new CarBookingConfirmCommand("", "1",
                sameDate,
                sameDate,
                VehicleCategoryEnum.LUXURY, PaymentModeEnum.CASH, ""
        );

        // WHEN / THEN
        assertDoesNotThrow(() -> validator.validate(command));
    }

    @Test
    void validate_shouldThrowException_whenStartDateIsAfterEndDate() {
        // GIVEN
        CarBookingConfirmCommand command = new CarBookingConfirmCommand("", "1",
                OffsetDateTime.now().plusDays(10),
                OffsetDateTime.now(),
                VehicleCategoryEnum.LUXURY, PaymentModeEnum.CASH, ""
        );

        // WHEN / THEN
        InvalidCarConfirmationException exception =
                assertThrows(
                        InvalidCarConfirmationException.class,
                        () -> validator.validate(command)
                );

        assertEquals(
                "The car booking start date cannot be greater than the end date.",
                exception.getMessage()
        );
    }
}