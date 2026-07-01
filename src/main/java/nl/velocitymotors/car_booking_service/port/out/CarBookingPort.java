package nl.velocitymotors.car_booking_service.port.out;

import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingSaved;

public interface CarBookingPort {

    CarBookingSaved saveBooking(CarBookingConfirmCommand command, String bookingStatus);
    void updateBookingPaymentStatus(String bookingId, String bookingStatus);
}
