package nl.velocitymotors.car_booking_service.messaging;

import nl.velocitymotors.car_booking_service.adapter.in.messaging.BankTransferPaymentUpdateMessageConsumer;
import nl.velocitymotors.car_booking_service.port.in.UpdateBookingPayedByBankTransferPort;
import nl.velocitymotors.carbooking.payment.avro.BankTransferPaymentCompletedEvent;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(classes = BankTransferPaymentConsumerIntegrationTest.KafkaTestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EmbeddedKafka(topics = BankTransferPaymentConsumerIntegrationTest.TOPIC, partitions = 1)
class BankTransferPaymentConsumerIntegrationTest {

    static final String TOPIC = "bank-transfer-payment-events";

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockitoBean
    private UpdateBookingPayedByBankTransferPort updateBookingPayedByBankTransfer;

    @Test
    void shouldConsumeAvroEventAndConfirmTheBooking() throws Exception {
        //given
        final var validKafkaEvent = paymentEvent("TXN987654321 BKG0000009");

        //when
        publish(validKafkaEvent);
        //then
        verify(updateBookingPayedByBankTransfer, timeout(10_000)).execute("BKG0000009");
    }

    @Test
    void shouldNotProcessAnInvalidPayloadButKeepConsuming() throws Exception {
        //given
        final var validKafkaEvent = paymentEvent("TXN987654321 BKG0000010");
        final var invalidKafkaEvent = "this-is-not-a-valid-avro-message".getBytes(StandardCharsets.UTF_8);

        //when
        publish(validKafkaEvent);
        publishRaw(invalidKafkaEvent);

        //then
        verify(updateBookingPayedByBankTransfer, timeout(10_000)).execute("BKG0000010");
        verifyNoMoreInteractions(updateBookingPayedByBankTransfer);
    }

    @Test
    void shouldForwardEachDeliveryOfADuplicatedMessage() throws Exception {
        //given
        final var duplicatedEvent = paymentEvent("TXN987654321 BKG0000011");

        //when
        publish(duplicatedEvent);
        publish(duplicatedEvent);

        verify(updateBookingPayedByBankTransfer, timeout(10_000).times(2)).execute("BKG0000011");
        verifyNoMoreInteractions(updateBookingPayedByBankTransfer);
    }

    private void publish(final BankTransferPaymentCompletedEvent event) throws Exception {
        publishRaw(serialize(event));
    }

    private void publishRaw(final byte[] payload) throws Exception {
        final Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        try (var producer = new KafkaProducer<>(producerProps, new StringSerializer(), new ByteArraySerializer())) {
            producer.send(new ProducerRecord<>(TOPIC, payload)).get();
        }
    }

    private static BankTransferPaymentCompletedEvent paymentEvent(final String transactionDetails) {
        return BankTransferPaymentCompletedEvent.newBuilder()
                .setPaymentId("PAY-1")
                .setSenderAccountNumber("NL00BANK0123456789")
                .setPaymentAmount(new BigDecimal("250.00"))
                .setTransactionDetails(transactionDetails)
                .build();
    }

    private static byte[] serialize(final BankTransferPaymentCompletedEvent event) throws Exception {
        final var writer = new SpecificDatumWriter<>(BankTransferPaymentCompletedEvent.class);
        final var out = new ByteArrayOutputStream();
        final var encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(event, encoder);
        encoder.flush();
        return out.toByteArray();
    }

    @Configuration
    @EnableAutoConfiguration(excludeName = {
            "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
            "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
    })
    @Import(BankTransferPaymentUpdateMessageConsumer.class)
    static class KafkaTestApp {
    }
}
