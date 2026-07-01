package nl.velocitymotors.car_booking_service.domain.exceptions;

public class PaymentNotConfirmedException extends RuntimeException {
    public PaymentNotConfirmedException(String message) {
        super(message);
    }
}
