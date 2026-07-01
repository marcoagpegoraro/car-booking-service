package nl.velocitymotors.car_booking_service.rooking;

import nl.velocitymotors.car_booking_service.adapter.out.booking.CarBookingAdapter;
import nl.velocitymotors.car_booking_service.adapter.out.booking.CarBookingJpaEntity;
import nl.velocitymotors.car_booking_service.adapter.out.booking.CarBookingJpaRepository;
import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.enums.VehicleCategoryEnum;
import nl.velocitymotors.car_booking_service.domain.model.Booking;
import nl.velocitymotors.car_booking_service.domain.model.RentalPeriod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarBookingAdapterTest {

    private static final OffsetDateTime START = OffsetDateTime.parse("2026-08-10T10:00:00Z");
    private static final OffsetDateTime END = OffsetDateTime.parse("2026-08-12T10:00:00Z");

    @Mock
    private CarBookingJpaRepository carBookingJpaRepository;

    @InjectMocks
    private CarBookingAdapter carBookingAdapter;

    private static CarBookingJpaEntity entity(final Long id, final String status) {
        final var entity = new CarBookingJpaEntity();
        entity.setId(id);
        entity.setCustomerName("Ana");
        entity.setVehicleID("VH-1");
        entity.setRentalStartDate(START);
        entity.setRentalEndDate(END);
        entity.setVehicleCategory("SUV");
        entity.setPaymentMode("BANK_TRANSFER");
        entity.setPaymentReference("ref");
        entity.setBookingStatus(status);
        return entity;
    }

    private static Booking newBooking() {
        return Booking.request("Ana", "VH-1", new RentalPeriod(START, END),
                VehicleCategoryEnum.SUV, PaymentModeEnum.BANK_TRANSFER, "ref");
    }

    @Test
    void save_shouldInsertNewBookingAndReturnItWithGeneratedId() {
        // GIVEN
        when(carBookingJpaRepository.save(any(CarBookingJpaEntity.class)))
                .thenReturn(entity(1L, "PENDING_PAYMENT"));

        // WHEN
        final Booking result = carBookingAdapter.save(newBooking());

        // THEN
        assertEquals(1L, result.getId());
        assertEquals(BookingStatusEnum.PENDING_PAYMENT, result.getStatus());
        verify(carBookingJpaRepository, never()).findById(any());
        verify(carBookingJpaRepository).save(any(CarBookingJpaEntity.class));
    }

    @Test
    void save_shouldApplyChangesOntoTheExistingManagedRow() {
        // GIVEN
        final Booking cancelled = Booking.reconstitute(5L, "Ana", "VH-1", new RentalPeriod(START, END),
                VehicleCategoryEnum.SUV, PaymentModeEnum.BANK_TRANSFER, "ref", BookingStatusEnum.CANCELLED);
        final CarBookingJpaEntity managedRow = entity(5L, "PENDING_PAYMENT");
        when(carBookingJpaRepository.findById(5L)).thenReturn(Optional.of(managedRow));
        when(carBookingJpaRepository.save(managedRow)).thenReturn(managedRow);

        // WHEN
        final Booking result = carBookingAdapter.save(cancelled);

        // THEN
        assertEquals("CANCELLED", managedRow.getBookingStatus());
        assertEquals(5L, result.getId());
        verify(carBookingJpaRepository).findById(5L);
        verify(carBookingJpaRepository).save(managedRow);
    }

    @Test
    void findById_shouldMapEntityToDomain() {
        when(carBookingJpaRepository.findById(7L)).thenReturn(Optional.of(entity(7L, "CONFIRMED")));

        final Optional<Booking> result = carBookingAdapter.findById(7L);

        assertTrue(result.isPresent());
        assertEquals(BookingStatusEnum.CONFIRMED, result.get().getStatus());
        assertEquals("VH-1", result.get().getVehicleId());
        assertEquals(START, result.get().getRentalPeriod().start());
    }

    @Test
    void findBankTransferBookingsAwaitingPaymentStartingBefore_shouldQueryAndMap() {
        final OffsetDateTime deadline = OffsetDateTime.parse("2026-07-03T00:00:00Z");
        when(carBookingJpaRepository.findByPaymentModeAndBookingStatusAndRentalStartDateLessThanEqual(
                "BANK_TRANSFER", "PENDING_PAYMENT", deadline))
                .thenReturn(List.of(entity(1L, "PENDING_PAYMENT"), entity(2L, "PENDING_PAYMENT")));

        final List<Booking> result = carBookingAdapter.findBankTransferBookingsAwaitingPaymentStartingBefore(deadline);

        assertEquals(2, result.size());
        verify(carBookingJpaRepository).findByPaymentModeAndBookingStatusAndRentalStartDateLessThanEqual(
                "BANK_TRANSFER", "PENDING_PAYMENT", deadline);
    }
}
