package nl.velocitymotors.car_booking_service.domain.exceptions;

public class IntegrationException extends RuntimeException {
    public IntegrationException(String message) {
        super(message);
    }
}
