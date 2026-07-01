package nl.velocitymotors.car_booking_service.validators;

import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.enums.VehicleCategoryEnum;
import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidCarConfirmationException;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.usecases.validators.CarBookingConfirmVehicleIDValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CarBookingConfirmVehicleIDValidatorTest {

    private CarBookingConfirmVehicleIDValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CarBookingConfirmVehicleIDValidator();
    }

    @Test
    void validate_shouldPass_whenVehicleIDIsPresent() {
        // GIVEN
        CarBookingConfirmCommand command = new CarBookingConfirmCommand("", "vehicle-123",
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(5),
                VehicleCategoryEnum.LUXURY, PaymentModeEnum.CASH, ""
        );

        // WHEN / THEN
        assertDoesNotThrow(() -> validator.validate(command));
    }

    @Test
    void validate_shouldThrowException_whenVehicleIDIsEmpty() {
        // GIVEN
        CarBookingConfirmCommand command = new CarBookingConfirmCommand("", "",
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(5),
                VehicleCategoryEnum.LUXURY, PaymentModeEnum.CASH, ""
        );

        // WHEN / THEN
        InvalidCarConfirmationException exception =
                assertThrows(
                        InvalidCarConfirmationException.class,
                        () -> validator.validate(command)
                );

        assertEquals(
                "The vehicle ID cannot be empty.",
                exception.getMessage()
        );
    }

    @Test
    void validate_shouldThrowException_whenVehicleIDIsBlank() {
        // GIVEN
        CarBookingConfirmCommand command = new CarBookingConfirmCommand("", "   ",
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(5),
                VehicleCategoryEnum.LUXURY, PaymentModeEnum.CASH, ""
        );

        // WHEN / THEN
        InvalidCarConfirmationException exception =
                assertThrows(
                        InvalidCarConfirmationException.class,
                        () -> validator.validate(command)
                );

        assertEquals(
                "The vehicle ID cannot be empty.",
                exception.getMessage()
        );
    }
}