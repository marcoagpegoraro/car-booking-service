package nl.velocitymotors.car_booking_service.adapter.in.messaging;

import lombok.extern.slf4j.Slf4j;
import nl.velocitymotors.carbookingservice.payment.avro.BankTransferPaymentCompletedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BankTransferPaymentUpdateMessageConsumer implements AvroMessageConsumer {

    @KafkaListener(
            topics = "${kafka.topic-name}",
            groupId = "${kafka.group-id}"
    )
    public void consume(final byte[] message) {
        final var event = deserialize(message, BankTransferPaymentCompletedEvent.class);

        System.out.println(event);
    }
}