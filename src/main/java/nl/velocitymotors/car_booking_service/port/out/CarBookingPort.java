package nl.velocitymotors.car_booking_service.port.out;

import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingSaved;

public interface CarBookingPort {

    CarBookingSaved saveBooking(final CarBookingConfirmCommand command, final BookingStatusEnum status);
    void updateBookingPaymentStatus(final String bookingId, final BookingStatusEnum status);
}
