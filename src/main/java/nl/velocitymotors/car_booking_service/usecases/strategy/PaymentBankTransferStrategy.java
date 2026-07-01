package nl.velocitymotors.car_booking_service.usecases.strategy;

import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingExecuted;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingSaved;
import nl.velocitymotors.car_booking_service.port.out.CarBookingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("BANK_TRANSFER")
@RequiredArgsConstructor
public class PaymentBankTransferStrategy implements PaymentStrategy {
    private final CarBookingPort carBookingPort;

    public CarBookingExecuted execute(CarBookingConfirmCommand command){
        final var bookingStatus = BookingStatusEnum.PENDING_PAYMENT;
        CarBookingSaved carBookingSaved = carBookingPort.saveBooking(command, bookingStatus);
        return new CarBookingExecuted(carBookingSaved.id(), bookingStatus);
    }
}
