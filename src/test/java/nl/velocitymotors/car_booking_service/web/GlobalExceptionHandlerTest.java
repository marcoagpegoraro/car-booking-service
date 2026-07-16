package nl.velocitymotors.car_booking_service.web;

import nl.velocitymotors.car_booking_service.adapter.in.web.config.GlobalExceptionHandler;
import nl.velocitymotors.car_booking_service.adapter.in.web.dto.ErrorResponse;
import nl.velocitymotors.car_booking_service.domain.exceptions.BookingNotFoundException;
import nl.velocitymotors.car_booking_service.domain.exceptions.IntegrationException;
import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidBookingStateException;
import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidCarConfirmationException;
import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidPaymentException;
import nl.velocitymotors.car_booking_service.domain.exceptions.PaymentNotConfirmedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void integrationFailureMapsToBadGateway() {
        final ResponseEntity<ErrorResponse> response =
                handler.handleIntegrationException(new IntegrationException("payment service down"));

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals("payment service down", response.getBody().error());
    }

    @Test
    void invalidPaymentMapsToBadRequest() {
        final ResponseEntity<ErrorResponse> response =
                handler.handleException(new InvalidPaymentException("invalid payment"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("invalid payment", response.getBody().error());
    }

    @Test
    void invalidCarConfirmationMapsToBadRequest() {
        final ResponseEntity<ErrorResponse> response =
                handler.handleException(new InvalidCarConfirmationException("vehicle ID cannot be empty"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("vehicle ID cannot be empty", response.getBody().error());
    }

    @Test
    void paymentNotConfirmedMapsToPaymentRequired() {
        final ResponseEntity<ErrorResponse> response =
                handler.handleException(new PaymentNotConfirmedException("credit card payment not confirmed"));

        assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
        assertEquals("credit card payment not confirmed", response.getBody().error());
    }

    @Test
    void bookingNotFoundMapsToNotFound() {
        final ResponseEntity<ErrorResponse> response =
                handler.handleException(new BookingNotFoundException("booking not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("booking not found", response.getBody().error());
    }

    @Test
    void invalidBookingStateMapsToConflict() {
        final ResponseEntity<ErrorResponse> response =
                handler.handleException(new InvalidBookingStateException("cannot confirm booking while it is CANCELLED"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("cannot confirm booking while it is CANCELLED", response.getBody().error());
    }
}
