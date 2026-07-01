package nl.velocitymotors.car_booking_service.adapter.in.web.config;

import lombok.extern.slf4j.Slf4j;
import nl.velocitymotors.car_booking_service.adapter.in.web.dto.ErrorResponse;
import nl.velocitymotors.car_booking_service.domain.exceptions.*;
import nl.velocitymotors.car_booking_service.domain.exceptions.IntegrationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrationException(IntegrationException ex) {
        log.error("Integration failure while confirming a booking", ex);
        final var  errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ErrorResponse> handleException(InvalidPaymentException ex) {
        log.warn("Invalid payment: {}", ex.getMessage());
        final var  errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCarConfirmationException.class)
    public ResponseEntity<ErrorResponse> handleException(InvalidCarConfirmationException ex) {
        log.warn("Invalid car booking confirmation: {}", ex.getMessage());
        final var  errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentNotConfirmedException.class)
    public ResponseEntity<ErrorResponse> handleException(PaymentNotConfirmedException ex) {
        log.warn("Payment not confirmed: {}", ex.getMessage());
        final var  errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.PAYMENT_REQUIRED);
    }
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(BookingNotFoundException ex) {
        log.warn("Booking not found: {}", ex.getMessage());
        final var  errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidBookingStateException.class)
    public ResponseEntity<ErrorResponse> handleException(InvalidBookingStateException ex) {
        log.warn("Invalid booking state transition: {}", ex.getMessage());
        final var  errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        log.warn("Unreadable request body: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(describeUnreadableBody(ex)));
    }

    private String describeUnreadableBody(final HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException formatException) {
            final String field = formatException.getPath().isEmpty()
                    ? "request body"
                    : formatException.getPath().getLast().getPropertyName();
            final Class<?> targetType = formatException.getTargetType();

            if (targetType != null && targetType.isEnum()) {
                final String accepted = Arrays.stream(targetType.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                return "Invalid value '%s' for field '%s'. Accepted values: [%s]"
                        .formatted(formatException.getValue(), field, accepted);
            }
            return "Invalid value '%s' for field '%s'".formatted(formatException.getValue(), field);
        }
        return "Malformed request body.";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Request validation failed: {}", ex.getMessage());
        final var errorResponse = new ErrorResponse(ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " "  + error.getDefaultMessage())
                .collect(Collectors.joining(". ")) + ".");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
