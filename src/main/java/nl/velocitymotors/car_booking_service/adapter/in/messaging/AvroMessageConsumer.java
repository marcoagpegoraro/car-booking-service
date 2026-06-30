package nl.velocitymotors.car_booking_service.adapter.in.messaging;

import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;

public interface AvroMessageConsumer {

    default <T extends SpecificRecord> T deserialize(final byte[] message, final Class<T> eventClass) {
        try {
            final var reader = new SpecificDatumReader<T>(eventClass);
            final var decoder = DecoderFactory.get().binaryDecoder(message, null);

            return reader.read(null, decoder);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to deserialize Avro event: " + eventClass.getSimpleName(), exception);
        }
    }
}