package nl.velocitymotors.car_booking_service.port.out;

import nl.velocitymotors.car_booking_service.domain.model.Booking;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface CarBookingPort {

    Booking save(final Booking booking);
    Optional<Booking> findById(final Long bookingId);
    List<Booking> findBankTransferBookingsAwaitingPaymentStartingBefore(final OffsetDateTime moment);
}
