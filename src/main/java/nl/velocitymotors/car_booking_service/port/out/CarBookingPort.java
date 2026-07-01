package nl.velocitymotors.car_booking_service.port.out;

import domain.model.CarBookingConfirmCommand;
import domain.model.CarBookingSaved;

public interface CarBookingPort {

    CarBookingSaved saveBooking(CarBookingConfirmCommand command, String bookingStatus);
    void updateBookingPaymentStatus(String bookingId, String bookingStatus);
}
