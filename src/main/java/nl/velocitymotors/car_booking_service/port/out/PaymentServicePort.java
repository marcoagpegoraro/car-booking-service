package nl.velocitymotors.car_booking_service.port.out;

import domain.model.PaymentDetails;

public interface PaymentServicePort {

    PaymentDetails getPaymentDetails(final String paymentId);
}
