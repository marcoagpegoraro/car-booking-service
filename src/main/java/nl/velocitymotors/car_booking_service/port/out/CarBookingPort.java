package nl.velocitymotors.car_booking_service.port.out;

import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingSaved;

import java.time.OffsetDateTime;
import java.util.List;

public interface CarBookingPort {

    CarBookingSaved saveBooking(final CarBookingConfirmCommand command, final BookingStatusEnum status);
    void updateBookingPaymentStatus(final Long bookingId, final BookingStatusEnum status);

    List<CarBookingSaved> findBookingsStartingBefore(
            final PaymentModeEnum paymentMode,
            final BookingStatusEnum status,
            final OffsetDateTime rentalStartDateThreshold);
}
