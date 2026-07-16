package nl.velocitymotors.car_booking_service.reservation;

import nl.velocitymotors.car_booking_service.adapter.out.booking.CarBookingJpaEntity;
import nl.velocitymotors.car_booking_service.adapter.out.booking.CarBookingJpaRepository;
import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class CarBookingJpaRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:16-alpine")
            .withInitScript("db/booking-reference-seq.sql");

    @Autowired
    private CarBookingJpaRepository carBookingJpaRepository;

    private static final String BANK_TRANSFER = PaymentModeEnum.BANK_TRANSFER.name();
    private static final String CREDIT_CARD = PaymentModeEnum.CREDIT_CARD.name();
    private static final String PENDING_PAYMENT = BookingStatusEnum.PENDING_PAYMENT.name();
    private static final String CONFIRMED = BookingStatusEnum.CONFIRMED.name();

    @Test
    void shouldReturnOnlyBankTransferPendingBookingsStartingOnOrBeforeTheDeadline() {
        //given
        final OffsetDateTime deadline = OffsetDateTime.parse("2026-07-03T00:00:00Z");

        carBookingJpaRepository.save(entity(BANK_TRANSFER, PENDING_PAYMENT, "2026-07-02T10:00:00Z", "VH-before"));
        carBookingJpaRepository.save(entity(BANK_TRANSFER, PENDING_PAYMENT, "2026-07-03T00:00:00Z", "VH-now"));
        carBookingJpaRepository.save(entity(BANK_TRANSFER, PENDING_PAYMENT, "2026-07-10T10:00:00Z", "VH-after"));
        carBookingJpaRepository.save(entity(BANK_TRANSFER, CONFIRMED, "2026-07-02T10:00:00Z", "VH-confirmed"));
        carBookingJpaRepository.save(entity(CREDIT_CARD, PENDING_PAYMENT, "2026-07-02T10:00:00Z", "VH-card"));

        //when
        final List<CarBookingJpaEntity> result = carBookingJpaRepository
                .findByPaymentModeAndBookingStatusAndRentalStartDateLessThanEqual(BANK_TRANSFER, PENDING_PAYMENT, deadline);

        //then
        final List<String> vehicleIds = result.stream().map(CarBookingJpaEntity::getVehicleID).toList();
        assertEquals(2, result.size());
        assertTrue(vehicleIds.containsAll(List.of("VH-before", "VH-now")));
    }

    @Test
    void shouldAssignABkgReferenceOnSave() {
        //when
        final CarBookingJpaEntity saved = carBookingJpaRepository.save(
                entity("BANK_TRANSFER", "PENDING_PAYMENT", "2026-07-02T10:00:00Z", "VH-1"));

        //then
        assertNotNull(saved.getId());
        assertEquals(10, saved.getId().length());
        assertTrue(saved.getId().contains("BKG"));
    }

    private static CarBookingJpaEntity entity(final String paymentMode, final String bookingStatus,
                                              final String rentalStart, final String vehicleId) {
        final var entity = new CarBookingJpaEntity();
        entity.setVehicleID(vehicleId);
        entity.setCustomerName("Marco");
        entity.setVehicleCategory("SUV");
        entity.setPaymentMode(paymentMode);
        entity.setPaymentReference("ref");
        entity.setBookingStatus(bookingStatus);
        entity.setRentalStartDate(OffsetDateTime.parse(rentalStart));
        entity.setRentalEndDate(OffsetDateTime.parse(rentalStart).plusDays(2));
        return entity;
    }
}
