package nl.velocitymotors.car_booking_service.domain;

import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidCarConfirmationException;
import nl.velocitymotors.car_booking_service.domain.model.RentalPeriod;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RentalPeriodTest {

    private static final OffsetDateTime START = OffsetDateTime.parse("2026-08-10T10:00:00Z");

    @Test
    void shouldAcceptAValidPeriod() {
        assertDoesNotThrow(() -> new RentalPeriod(START, START.plusDays(5)));
    }

    @Test
    void shouldAcceptExactlyTheMaximumOf21Days() {
        assertDoesNotThrow(() -> new RentalPeriod(START, START.plusDays(21)));
    }

    @Test
    void shouldRejectWhenEndIsBeforeStart() {
        assertThrows(InvalidCarConfirmationException.class, () -> new RentalPeriod(START, START.minusDays(1)));
    }

    @Test
    void shouldRejectMoreThan21Days() {
        assertThrows(InvalidCarConfirmationException.class, () -> new RentalPeriod(START, START.plusDays(22)));
    }

    @Test
    void shouldRejectNullDates() {
        assertThrows(InvalidCarConfirmationException.class, () -> new RentalPeriod(null, START));
    }
}
