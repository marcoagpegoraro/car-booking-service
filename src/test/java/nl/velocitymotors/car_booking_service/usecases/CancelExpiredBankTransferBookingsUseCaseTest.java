package nl.velocitymotors.car_booking_service.usecases;

import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.enums.VehicleCategoryEnum;
import nl.velocitymotors.car_booking_service.domain.model.Booking;
import nl.velocitymotors.car_booking_service.domain.model.RentalPeriod;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    @Captor
    private ArgumentCaptor<Booking> savedCaptor;

    private CancelExpiredBankTransferBookingsUseCase useCase() {
        return new CancelExpiredBankTransferBookingsUseCase(carBookingPort, FIXED_CLOCK, PAYMENT_WINDOW_HOURS);
    }

    private static Booking pendingBankTransfer(final Long id) {
        return Booking.reconstitute(id, "cust", "VH",
                new RentalPeriod(OffsetDateTime.parse("2026-07-02T10:00:00Z"), OffsetDateTime.parse("2026-07-03T10:00:00Z")),
                VehicleCategoryEnum.SUV, PaymentModeEnum.BANK_TRANSFER, "ref", BookingStatusEnum.PENDING_PAYMENT);
    }

    @Test
    void shouldCancelAndSaveEachDueBooking() {
        when(carBookingPort.findBankTransferBookingsAwaitingPaymentStartingBefore(deadlineCaptor.capture()))
                .thenReturn(List.of(pendingBankTransfer(1L), pendingBankTransfer(2L)));

        final int cancelled = useCase().execute();

        // THEN the deadline is now + 48h, and every due booking is cancelled and saved
        assertEquals(2, cancelled);
        assertEquals(EXPECTED_DEADLINE, deadlineCaptor.getValue());
        verify(carBookingPort, times(2)).save(savedCaptor.capture());
        assertTrue(savedCaptor.getAllValues().stream()
                .allMatch(booking -> booking.getStatus() == BookingStatusEnum.CANCELLED));
    }

    @Test
    void shouldDoNothingWhenNoBookingsAreDue() {
        when(carBookingPort.findBankTransferBookingsAwaitingPaymentStartingBefore(deadlineCaptor.capture()))
                .thenReturn(List.of());

        final int cancelled = useCase().execute();

        assertEquals(0, cancelled);
        verify(carBookingPort, never()).save(any());
    }
}
