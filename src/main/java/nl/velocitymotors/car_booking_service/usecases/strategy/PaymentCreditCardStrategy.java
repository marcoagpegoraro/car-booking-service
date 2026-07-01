package nl.velocitymotors.car_booking_service.usecases.strategy;

import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentStatusEnum;
import nl.velocitymotors.car_booking_service.domain.exceptions.PaymentNotConfirmedException;
import nl.velocitymotors.car_booking_service.domain.model.PaymentDetails;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingExecuted;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingSaved;
import nl.velocitymotors.car_booking_service.port.out.PaymentServicePort;
import nl.velocitymotors.car_booking_service.port.out.CarBookingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("CREDIT_CARD")
@RequiredArgsConstructor
public class PaymentCreditCardStrategy implements PaymentStrategy {

    private final PaymentServicePort paymentServicePort;
    private final CarBookingPort carBookingPort;

    public CarBookingExecuted execute(CarBookingConfirmCommand command){
        PaymentDetails paymentDetails = paymentServicePort.getPaymentDetails(command.paymentReference());

        if(PaymentStatusEnum.APPROVED.equals(paymentDetails.status())){
            final var bookingStatus = BookingStatusEnum.CONFIRMED;
            CarBookingSaved carBookingSaved = carBookingPort.saveBooking(command, bookingStatus);
            return new CarBookingExecuted(carBookingSaved.id(), bookingStatus);
        }

        throw new PaymentNotConfirmedException("The credit card payment is not confirmed.");
    }
}
