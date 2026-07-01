package nl.velocitymotors.car_booking_service.domain;

import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.enums.VehicleCategoryEnum;
import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidBookingStateException;
import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidCarConfirmationException;
import nl.velocitymotors.car_booking_service.domain.model.Booking;
import nl.velocitymotors.car_booking_service.domain.model.RentalPeriod;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingTest {

    private static final RentalPeriod PERIOD = new RentalPeriod(
            OffsetDateTime.parse("2026-08-10T10:00:00Z"), OffsetDateTime.parse("2026-08-12T10:00:00Z"));

    private static Booking requested() {
        return Booking.request("Ana", "VH-1", PERIOD, VehicleCategoryEnum.SUV, PaymentModeEnum.BANK_TRANSFER, "ref");
    }

    @Test
    void requestShouldStartAwaitingPaymentWithNoId() {
        final Booking booking = requested();
        assertNull(booking.getId());
        assertEquals(BookingStatusEnum.PENDING_PAYMENT, booking.getStatus());
    }

    @Test
    void requestShouldRejectABlankVehicleId() {
        assertThrows(InvalidCarConfirmationException.class,
                () -> Booking.request("Ana", " ", PERIOD, VehicleCategoryEnum.SUV, PaymentModeEnum.CASH, "ref"));
    }

    @Test
    void confirmPaymentShouldMovePendingToConfirmed() {
        final Booking booking = requested();
        booking.confirmPayment();
        assertEquals(BookingStatusEnum.CONFIRMED, booking.getStatus());
    }

    @Test
    void confirmPaymentShouldBeIdempotentWhenAlreadyConfirmed() {
        final Booking booking = requested();
        booking.confirmPayment();
        assertDoesNotThrow(booking::confirmPayment);
        assertEquals(BookingStatusEnum.CONFIRMED, booking.getStatus());
    }

    @Test
    void confirmPaymentShouldRejectACancelledBooking() {
        final Booking booking = requested();
        booking.cancel();
        assertThrows(InvalidBookingStateException.class, booking::confirmPayment);
    }

    @Test
    void cancelShouldMovePendingToCancelled() {
        final Booking booking = requested();
        booking.cancel();
        assertEquals(BookingStatusEnum.CANCELLED, booking.getStatus());
    }

    @Test
    void cancelShouldBeIdempotentWhenAlreadyCancelled() {
        final Booking booking = requested();
        booking.cancel();
        assertDoesNotThrow(booking::cancel);
        assertEquals(BookingStatusEnum.CANCELLED, booking.getStatus());
    }

    @Test
    void cancelShouldRejectAConfirmedBooking() {
        final Booking booking = requested();
        booking.confirmPayment();
        assertThrows(InvalidBookingStateException.class, booking::cancel);
    }
}
