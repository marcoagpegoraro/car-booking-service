package nl.velocitymotors.car_booking_service.web;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nl.velocitymotors.car_booking_service.adapter.in.web.dto.CarBookingConfirmRequest;
import nl.velocitymotors.car_booking_service.adapter.in.web.dto.PaymentModeRequestEnum;
import nl.velocitymotors.car_booking_service.adapter.in.web.dto.VehicleCategoryRequestEnum;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CarBookingConfirmRequestValidationTest {

    private static final OffsetDateTime START = OffsetDateTime.parse("2026-09-10T10:00:00Z");
    private static final OffsetDateTime END = OffsetDateTime.parse("2026-09-12T10:00:00Z");

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void shouldAValidRequestWorkWithoutAnyIssues() {
        final var request = new CarBookingConfirmRequest("Marco", "VH-1", START, END,
                VehicleCategoryRequestEnum.SUV, PaymentModeRequestEnum.CASH, "ref");

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void everyRequiredFieldIsFlaggedWhenNull() {
        final var request = new CarBookingConfirmRequest(null, null, null, null, null, null, null);

        final Set<String> violatedFields = validator.validate(request).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());

        assertEquals(6, violatedFields.size());
        assertTrue(violatedFields.contains("customerName"));
        assertTrue(violatedFields.contains("vehicleID"));
        assertTrue(violatedFields.contains("rentalStartDate"));
        assertTrue(violatedFields.contains("rentalEndDate"));
        assertTrue(violatedFields.contains("vehicleCategory"));
        assertTrue(violatedFields.contains("paymentMode"));
    }

    @Test
    void paymentReferenceIsOptional() {
        final var request = new CarBookingConfirmRequest("Marco", "VH-1", START, END,
                VehicleCategoryRequestEnum.SUV, PaymentModeRequestEnum.BANK_TRANSFER, null);

        assertTrue(validator.validate(request).isEmpty());
    }
}
