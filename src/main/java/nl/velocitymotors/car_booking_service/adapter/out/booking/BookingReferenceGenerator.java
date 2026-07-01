package nl.velocitymotors.car_booking_service.adapter.out.booking;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.sql.ResultSet;
import java.sql.Statement;

public class BookingReferenceGenerator implements IdentifierGenerator {

    private static volatile boolean sequenceCreated = false;

    @Override
    public Object generate(final SharedSessionContractImplementor session, final Object object) {
        return session.doReturningWork(connection -> {
            try (Statement statement = connection.createStatement()) {
                if (!sequenceCreated) {
                    statement.execute("CREATE SEQUENCE IF NOT EXISTS booking_reference_seq START WITH 1 INCREMENT BY 1");
                    sequenceCreated = true;
                }
                try (ResultSet resultSet = statement.executeQuery("SELECT nextval('booking_reference_seq')")) {
                    resultSet.next();
                    return "BKG%07d".formatted(resultSet.getLong(1));
                }
            }
        });
    }
}
