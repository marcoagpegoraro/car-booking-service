package nl.velocitymotors.car_booking_service.usecases;

import lombok.extern.slf4j.Slf4j;
import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingSaved;
import nl.velocitymotors.car_booking_service.port.in.CancelExpiredBankTransferBookingsPort;
import nl.velocitymotors.car_booking_service.port.out.CarBookingPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
public class CancelExpiredBankTransferBookingsUseCase implements CancelExpiredBankTransferBookingsPort {

    private final CarBookingPort carBookingPort;
    private final Clock clock;
    private final long paymentWindowHours;

    public CancelExpiredBankTransferBookingsUseCase(
            final CarBookingPort carBookingPort,
            final Clock clock,
            @Value("${booking.cancellation.payment-window-hours}") final long paymentWindowHours) {
        this.carBookingPort = carBookingPort;
        this.clock = clock;
        this.paymentWindowHours = paymentWindowHours;
    }

    @Override
    @Transactional
    public int execute() {
        final OffsetDateTime deadline = OffsetDateTime.now(clock).plusHours(paymentWindowHours);

        final List<CarBookingSaved> expiredBookings = carBookingPort.findBookingsStartingBefore(
                PaymentModeEnum.BANK_TRANSFER, BookingStatusEnum.PENDING_PAYMENT, deadline);

        expiredBookings.forEach(booking -> {
            log.info("Auto cancelling booking {}: bank transfer not paid at least {}h before rental start",
                    booking.id(), paymentWindowHours);
            carBookingPort.updateBookingPaymentStatus(booking.id(), BookingStatusEnum.CANCELLED);
        });

        return expiredBookings.size();
    }
}
