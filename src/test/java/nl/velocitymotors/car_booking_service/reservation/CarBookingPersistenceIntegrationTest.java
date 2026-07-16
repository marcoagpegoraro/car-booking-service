package nl.velocitymotors.car_booking_service.reservation;

import nl.velocitymotors.car_booking_service.adapter.out.booking.CarBookingAdapter;
import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.enums.VehicleCategoryEnum;
import nl.velocitymotors.car_booking_service.domain.model.Booking;
import nl.velocitymotors.car_booking_service.domain.model.RentalPeriod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Import(CarBookingAdapter.class)
class CarBookingPersistenceIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:16-alpine")
            .withInitScript("db/booking-reference-seq.sql");

    @Autowired
    private CarBookingAdapter carBookingAdapter;

    @Test
    void save_generatesBkgReferenceAndPersistsTheBooking() {
        final var saved = carBookingAdapter.save(
                bankTransferPending("Alice", "VH-1", period("2026-07-02T10:00:00Z", "2026-07-04T10:00:00Z")));

        assertNotNull(saved.getId());
        assertEquals(10, saved.getId().length());
        assertTrue(saved.getId().contains("BKG"));

        final var reloaded = carBookingAdapter.findById(saved.getId());
        assertTrue(reloaded.isPresent());
        assertEquals("VH-1", reloaded.get().getVehicleId());
        assertEquals(PaymentModeEnum.BANK_TRANSFER, reloaded.get().getPaymentMode());
        assertEquals(BookingStatusEnum.PENDING_PAYMENT, reloaded.get().getStatus());
        assertEquals(OffsetDateTime.parse("2026-07-02T10:00:00Z"), reloaded.get().getRentalPeriod().start());
    }

    @Test
    void save_persistsStatusTransitions() {
        final var saved = carBookingAdapter.save(
                bankTransferPending("Bob", "VH-2", period("2026-07-02T10:00:00Z", "2026-07-04T10:00:00Z")));

        final var loaded = carBookingAdapter.findById(saved.getId()).orElseThrow();
        loaded.confirmPayment();
        carBookingAdapter.save(loaded);

        assertEquals(BookingStatusEnum.CONFIRMED,
                carBookingAdapter.findById(saved.getId()).orElseThrow().getStatus());
    }

    @Test
    void findBankTransferBookingsAwaitingPaymentStartingBefore_appliesTheRealDerivedQuery() {
        //given
        final OffsetDateTime deadline = OffsetDateTime.parse("2026-07-03T00:00:00Z");

        final var bankTransferPendingStartsBeforeDeadline=  bankTransferPending("match", "VH-match", period("2026-07-02T10:00:00Z", "2026-07-04T10:00:00Z"));
        final var bankTransferPendingStartsAfterDeadline= bankTransferPending("late", "VH-late", period("2026-07-10T10:00:00Z", "2026-07-12T10:00:00Z"));
        final var bankTransferConfirmedStartsBeforeDeadline=bankTransferPending("confirmed", "VH-conf", period("2026-07-02T10:00:00Z", "2026-07-04T10:00:00Z"));
        bankTransferConfirmedStartsBeforeDeadline.confirmPayment();

        final var invalidCreditCardWithPayment = Booking.request("card", "VH-card", period("2026-07-02T10:00:00Z", "2026-07-04T10:00:00Z"),
                VehicleCategoryEnum.SUV, PaymentModeEnum.CREDIT_CARD, "ref");

        //when
        carBookingAdapter.save(bankTransferPendingStartsBeforeDeadline);
        carBookingAdapter.save(bankTransferPendingStartsAfterDeadline);
        carBookingAdapter.save(bankTransferConfirmedStartsBeforeDeadline);
        carBookingAdapter.save(invalidCreditCardWithPayment);

        final var due = carBookingAdapter.findBankTransferBookingsAwaitingPaymentStartingBefore(deadline);

        //then
        assertEquals(1, due.size());
        assertEquals("VH-match", due.getFirst().getVehicleId());
    }


    private static RentalPeriod period(final String start, final String end) {
        return new RentalPeriod(OffsetDateTime.parse(start), OffsetDateTime.parse(end));
    }

    private static Booking bankTransferPending(final String customer, final String vehicleId, final RentalPeriod period) {
        return Booking.request(customer, vehicleId, period, VehicleCategoryEnum.SUV, PaymentModeEnum.BANK_TRANSFER, "ref");
    }
}
