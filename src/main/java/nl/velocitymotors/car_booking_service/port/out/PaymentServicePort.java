package nl.velocitymotors.car_booking_service.port.out;

import nl.velocitymotors.car_booking_service.domain.model.PaymentDetails;

public interface PaymentServicePort {

    PaymentDetails getPaymentDetails(final String paymentId);
}
