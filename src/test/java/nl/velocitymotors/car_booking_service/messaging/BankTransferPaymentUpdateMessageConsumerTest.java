package nl.velocitymotors.car_booking_service.messaging;

import nl.velocitymotors.car_booking_service.adapter.in.messaging.BankTransferPaymentUpdateMessageConsumer;
import nl.velocitymotors.car_booking_service.port.in.UpdateBookingPayedByBankTransferPort;
import nl.velocitymotors.carbooking.payment.avro.BankTransferPaymentCompletedEvent;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BankTransferPaymentUpdateMessageConsumerTest {

    @Mock
    private UpdateBookingPayedByBankTransferPort updateBookingPayedByBankTransfer;

    @InjectMocks
    private BankTransferPaymentUpdateMessageConsumer consumer;

    private static byte[] serialize(final BankTransferPaymentCompletedEvent event) throws Exception {
        final var writer = new SpecificDatumWriter<>(BankTransferPaymentCompletedEvent.class);
        final var out = new ByteArrayOutputStream();
        final var encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(event, encoder);
        encoder.flush();
        return out.toByteArray();
    }

    private static BankTransferPaymentCompletedEvent event(final String transactionDetails) {
        return BankTransferPaymentCompletedEvent.newBuilder()
                .setPaymentId("PAY-1")
                .setSenderAccountNumber("NL00BANK0123456789")
                .setPaymentAmount(new BigDecimal("250.00"))
                .setTransactionDetails(transactionDetails)
                .build();
    }

    @Test
    void shouldConfirmBookingExtractedFromTransactionDetails() throws Exception {
        // GIVEN "<TxnRef> <BookingId>"
        final byte[] message = serialize(event("TXN987654321 BKG0012345"));

        // WHEN
        consumer.consume(message);

        // THEN the booking id is forwarded to the update use case
        verify(updateBookingPayedByBankTransfer).execute("BKG0012345");
    }

    @Test
    void shouldRejectMalformedTransactionDetails() throws Exception {
        final byte[] message = serialize(event("MISSING_BOOKING_ID"));

        assertThrows(IllegalArgumentException.class, () -> consumer.consume(message));
        verify(updateBookingPayedByBankTransfer, never()).execute(org.mockito.ArgumentMatchers.anyString());
    }
}
