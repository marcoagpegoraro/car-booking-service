package nl.velocitymotors.car_booking_service.usecases;

import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.port.in.UpdateBookingPayedByBankTransferPort;
import nl.velocitymotors.car_booking_service.port.out.CarBookingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateBookingPayedByBankTransferPaymentUseCase implements UpdateBookingPayedByBankTransferPort {

    private final CarBookingPort carBookingPort;

    @Override
    public void execute(final Long bookingId) {
        carBookingPort.updateBookingPaymentStatus(bookingId, BookingStatusEnum.CONFIRMED);
    }
}
