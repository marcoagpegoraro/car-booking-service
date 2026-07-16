package nl.velocitymotors.car_booking_service.adapter.in.messaging;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    // https://avro.apache.org/docs/1.8.2/api/java/org/apache/avro/generic/GenericDatumReader.html
    default GenericRecord deserializeFields(final byte[] message, final Schema writerSchema, final String... fields) {
        try {
            final Schema readerSchema = projectionOf(writerSchema, fields);
            final var reader = new GenericDatumReader<GenericRecord>(writerSchema, readerSchema);
            final var decoder = DecoderFactory.get().binaryDecoder(message, null);

            return reader.read(null, decoder);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to deserialize Avro fields " + Arrays.toString(fields), exception);
        }
    }

    private static Schema projectionOf(final Schema writerSchema, final String... fields) {
        final List<Schema.Field> projectedFields = Arrays.stream(fields)
                .map(name -> Objects.requireNonNull(writerSchema.getField(name), () -> "Unknown Avro field: " + name))
                .map(field -> new Schema.Field(field.name(), field.schema(), field.doc(), field.defaultVal()))
                .toList();

        return Schema.createRecord(writerSchema.getName(), writerSchema.getDoc(),
                writerSchema.getNamespace(), writerSchema.isError(), projectedFields);
    }
}
