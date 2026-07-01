package nl.velocitymotors.car_booking_service.usecases.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentStatusEnum;
import nl.velocitymotors.car_booking_service.domain.exceptions.PaymentNotConfirmedException;
import nl.velocitymotors.car_booking_service.domain.model.Booking;
import nl.velocitymotors.car_booking_service.domain.model.PaymentDetails;
import nl.velocitymotors.car_booking_service.port.out.PaymentServicePort;
import org.springframework.stereotype.Service;

@Slf4j
@Service("CREDIT_CARD")
@RequiredArgsConstructor
public class PaymentCreditCardStrategy implements PaymentStrategy {

    private final PaymentServicePort paymentServicePort;

    @Override
    public void apply(final Booking booking) {
        final PaymentDetails paymentDetails = paymentServicePort.getPaymentDetails(booking.getPaymentReference());

        if (PaymentStatusEnum.APPROVED.equals(paymentDetails.status())) {
            booking.confirmPayment();
            return;
        }

        log.warn("Credit card payment not approved (status {}) for reference {}. rejecting booking",
                paymentDetails.status(), booking.getPaymentReference());
        throw new PaymentNotConfirmedException("The credit card payment is not confirmed");
    }
}
