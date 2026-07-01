package nl.velocitymotors.car_booking_service.domain.model;

import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;

public record CarBookingExecuted(
        String BookingId,
        BookingStatusEnum bookingStatus
){
}
