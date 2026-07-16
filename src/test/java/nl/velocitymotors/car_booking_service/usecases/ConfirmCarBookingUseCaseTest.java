package nl.velocitymotors.car_booking_service.usecases;

import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.VehicleCategoryEnum;
import nl.velocitymotors.car_booking_service.domain.exceptions.PaymentNotConfirmedException;
import nl.velocitymotors.car_booking_service.domain.model.Booking;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.PaymentDetails;
import nl.velocitymotors.car_booking_service.port.out.CarBookingPort;
import nl.velocitymotors.car_booking_service.port.out.PaymentServicePort;
import nl.velocitymotors.car_booking_service.usecases.strategy.PaymentBankTransferStrategy;
import nl.velocitymotors.car_booking_service.usecases.strategy.PaymentCashStrategy;
import nl.velocitymotors.car_booking_service.usecases.strategy.PaymentCreditCardStrategy;
import nl.velocitymotors.car_booking_service.usecases.strategy.PaymentFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmCarBookingUseCaseTest {

    private static final OffsetDateTime START = OffsetDateTime.parse("2026-09-10T10:00:00Z");
    private static final OffsetDateTime END = OffsetDateTime.parse("2026-09-12T10:00:00Z");

    @Mock
    private CarBookingPort carBookingPort;

    @Mock
    private PaymentServicePort paymentServicePort;

    @Captor
    private ArgumentCaptor<Booking> savedBookingCaptor;

    private ConfirmCarBookingUseCase confirmCarBookingUseCase;

    @BeforeEach
    public void setup(){
        final PaymentFactory paymentFactory = new PaymentFactory(Map.of(
                PaymentModeEnum.CASH.name(), new PaymentCashStrategy(),
                PaymentModeEnum.BANK_TRANSFER.name(), new PaymentBankTransferStrategy(),
                PaymentModeEnum.CREDIT_CARD.name(), new PaymentCreditCardStrategy(paymentServicePort)));
        this.confirmCarBookingUseCase = new ConfirmCarBookingUseCase(paymentFactory, carBookingPort);
    }

    private static CarBookingConfirmCommand command(final PaymentModeEnum paymentMode, final String paymentReference) {
        return new CarBookingConfirmCommand("Alice", "VH-1", START, END,
                VehicleCategoryEnum.SUV, paymentMode, paymentReference);
    }

    @Test
    void shouldPersistBookingWithConfirmedStatusComputedByTheCashRule() {
        when(carBookingPort.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        confirmCarBookingUseCase.execute(command(PaymentModeEnum.CASH, null));

        verify(carBookingPort).save(savedBookingCaptor.capture());
        final Booking persisted = savedBookingCaptor.getValue();
        assertEquals(BookingStatusEnum.CONFIRMED, persisted.getStatus());
        assertEquals("VH-1", persisted.getVehicleId());
        assertEquals(PaymentModeEnum.CASH, persisted.getPaymentMode());
        assertEquals(START, persisted.getRentalPeriod().start());
    }

    @Test
    void shouldPersistBookingWithPendingStatusComputedByTheBankTransferRule() {
        when(carBookingPort.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        confirmCarBookingUseCase.execute(command(PaymentModeEnum.BANK_TRANSFER, null));

        verify(carBookingPort).save(savedBookingCaptor.capture());
        assertEquals(BookingStatusEnum.PENDING_PAYMENT, savedBookingCaptor.getValue().getStatus());
    }

    @Test
    void shouldPersistConfirmedStatusWhenTheCreditCardIsApproved() {
        when(paymentServicePort.getPaymentDetails("DL123456789"))
                .thenReturn(new PaymentDetails(null, PaymentStatusEnum.APPROVED));
        when(carBookingPort.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        confirmCarBookingUseCase.execute(command(PaymentModeEnum.CREDIT_CARD, "DL123456789"));

        verify(carBookingPort).save(savedBookingCaptor.capture());
        assertEquals(BookingStatusEnum.CONFIRMED, savedBookingCaptor.getValue().getStatus());
    }

    @Test
    void shouldNotPersistAnythingWhenTheCreditCardIsRejected() {
        when(paymentServicePort.getPaymentDetails("DL987654321"))
                .thenReturn(new PaymentDetails(null, PaymentStatusEnum.REJECTED));

        assertThrows(PaymentNotConfirmedException.class,
                () -> confirmCarBookingUseCase.execute(command(PaymentModeEnum.CREDIT_CARD, "DL987654321")));

        verify(carBookingPort, never()).save(any());
    }
}
