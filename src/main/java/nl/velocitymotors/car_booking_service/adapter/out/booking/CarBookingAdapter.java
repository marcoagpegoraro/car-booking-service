package nl.velocitymotors.car_booking_service.adapter.out.booking;

import lombok.RequiredArgsConstructor;
import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.enums.VehicleCategoryEnum;
import nl.velocitymotors.car_booking_service.domain.model.Booking;
import nl.velocitymotors.car_booking_service.domain.model.RentalPeriod;
import nl.velocitymotors.car_booking_service.port.out.CarBookingPort;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CarBookingAdapter implements CarBookingPort {

    private final CarBookingJpaRepository carBookingJpaRepository;

    @Override
    public Booking save(final Booking booking) {
        final CarBookingJpaEntity entity = booking.getId() == null
                ? new CarBookingJpaEntity()
                : carBookingJpaRepository.findById(booking.getId()).orElseGet(CarBookingJpaEntity::new);

        applyToEntity(booking, entity);
        return toDomain(carBookingJpaRepository.save(entity));
    }

    @Override
    public Optional<Booking> findById(final Long bookingId) {
        return carBookingJpaRepository.findById(bookingId).map(this::toDomain);
    }

    @Override
    public List<Booking> findBankTransferBookingsAwaitingPaymentStartingBefore(final OffsetDateTime moment) {
        return carBookingJpaRepository
                .findByPaymentModeAndBookingStatusAndRentalStartDateLessThanEqual(
                        PaymentModeEnum.BANK_TRANSFER.name(), BookingStatusEnum.PENDING_PAYMENT.name(), moment)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private void applyToEntity(final Booking booking, final CarBookingJpaEntity entity) {
        entity.setCustomerName(booking.getCustomerName());
        entity.setVehicleID(booking.getVehicleId());
        entity.setRentalStartDate(booking.getRentalPeriod().start());
        entity.setRentalEndDate(booking.getRentalPeriod().end());
        entity.setVehicleCategory(booking.getVehicleCategory().name());
        entity.setPaymentMode(booking.getPaymentMode().name());
        entity.setPaymentReference(booking.getPaymentReference());
        entity.setBookingStatus(booking.getStatus().name());
    }

    private Booking toDomain(final CarBookingJpaEntity entity) {
        return Booking.reconstitute(
                entity.getId(),
                entity.getCustomerName(),
                entity.getVehicleID(),
                new RentalPeriod(entity.getRentalStartDate(), entity.getRentalEndDate()),
                VehicleCategoryEnum.valueOf(entity.getVehicleCategory()),
                PaymentModeEnum.valueOf(entity.getPaymentMode()),
                entity.getPaymentReference(),
                BookingStatusEnum.valueOf(entity.getBookingStatus()));
    }
}
