package nl.velocitymotors.car_booking_service.domain.exceptions;

public class InvalidCarConfirmationException extends RuntimeException {
    public InvalidCarConfirmationException(String message) {
        super(message);
    }
}
