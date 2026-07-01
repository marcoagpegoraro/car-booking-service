package nl.velocitymotors.car_booking_service.adapter.in.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.velocitymotors.car_booking_service.domain.exceptions.BookingNotFoundException;
import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidBookingStateException;
import nl.velocitymotors.car_booking_service.port.in.UpdateBookingPayedByBankTransferPort;
import nl.velocitymotors.carbooking.payment.avro.BankTransferPaymentCompletedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankTransferPaymentUpdateMessageConsumer implements AvroMessageConsumer {

    private final UpdateBookingPayedByBankTransferPort updateBookingPayedByBankTransfer;

    @KafkaListener(
            topics = "${kafka.topic-name}",
            groupId = "${kafka.group-id}"
    )
    public void consume(final byte[] message) {
        final var event = deserialize(message, BankTransferPaymentCompletedEvent.class);
        final String bookingId = extractBookingId(event.getTransactionDetails());

        log.info("Received bank transfer payment {} for booking {}", event.getPaymentId(), bookingId);

        try {
            updateBookingPayedByBankTransfer.execute(bookingId);
        } catch (final BookingNotFoundException exception) {
            log.warn("Received a bank transfer payment for an unknown booking {}", bookingId, exception);
        } catch (final InvalidBookingStateException exception) {
            // e.g. a payment arriving for an already-cancelled booking; log and move on.
            log.warn("Cannot apply bank transfer payment to booking {}: {}", bookingId, exception.getMessage());
        }
    }

    private static String extractBookingId(final String transactionDetails) {
        final String[] parts = transactionDetails == null ? new String[0] : transactionDetails.trim().split("\\s+");

        if (parts.length < 2) {
            throw new IllegalArgumentException(
                    "Unexpected transactionDetails, expected \"<TxnRef> <BookingId>\" but got: " + transactionDetails);
        }

        return parts[parts.length - 1];
    }
}
