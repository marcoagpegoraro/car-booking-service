package nl.velocitymotors.car_booking_service.usecases;

import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingSaved;
import nl.velocitymotors.car_booking_service.port.out.CarBookingPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelExpiredBankTransferBookingsUseCaseTest {

    private static final long PAYMENT_WINDOW_HOURS = 48;
    // "Now" is fixed, so the 48h deadline is 2026-07-03T00:00:00Z.
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-07-01T00:00:00Z"), ZoneOffset.UTC);
    private static final OffsetDateTime EXPECTED_DEADLINE = OffsetDateTime.parse("2026-07-03T00:00:00Z");

    @Mock
    private CarBookingPort carBookingPort;

    @Captor
    private ArgumentCaptor<OffsetDateTime> deadlineCaptor;

    private CancelExpiredBankTransferBookingsUseCase useCase() {
        return new CancelExpiredBankTransferBookingsUseCase(carBookingPort, FIXED_CLOCK, PAYMENT_WINDOW_HOURS);
    }

    private static CarBookingSaved bookingWithId(final Long id) {
        return new CarBookingSaved(id, "", null, null, "", "", "", "", "");
    }

    @Test
    void shouldCancelEachExpiredBankTransferBooking() {
        // GIVEN two bank-transfer bookings still pending payment inside the window
        when(carBookingPort.findBookingsStartingBefore(
                eq(PaymentModeEnum.BANK_TRANSFER), eq(BookingStatusEnum.PENDING_PAYMENT), deadlineCaptor.capture()))
                .thenReturn(List.of(bookingWithId(1L), bookingWithId(2L)));

        // WHEN
        final int cancelled = useCase().execute();

        // THEN each booking is moved to CANCELLED and the count is returned
        assertEquals(2, cancelled);
        verify(carBookingPort).updateBookingPaymentStatus(1L, BookingStatusEnum.CANCELLED);
        verify(carBookingPort).updateBookingPaymentStatus(2L, BookingStatusEnum.CANCELLED);
    }

    @Test
    void shouldQueryUsingDeadline48HoursFromNow() {
        when(carBookingPort.findBookingsStartingBefore(
                eq(PaymentModeEnum.BANK_TRANSFER), eq(BookingStatusEnum.PENDING_PAYMENT), deadlineCaptor.capture()))
                .thenReturn(List.of());

        useCase().execute();

        assertEquals(EXPECTED_DEADLINE, deadlineCaptor.getValue());
    }

    @Test
    void shouldDoNothingWhenNoBookingsAreExpired() {
        when(carBookingPort.findBookingsStartingBefore(
                eq(PaymentModeEnum.BANK_TRANSFER), eq(BookingStatusEnum.PENDING_PAYMENT), deadlineCaptor.capture()))
                .thenReturn(List.of());

        final int cancelled = useCase().execute();

        assertEquals(0, cancelled);
        verify(carBookingPort, never()).updateBookingPaymentStatus(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any());
    }
}
