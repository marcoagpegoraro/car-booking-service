package nl.velocitymotors.car_booking_service.domain.model;

import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;

public record CarBookingExecuted(
        Long bookingId,
        BookingStatusEnum bookingStatus
){
}
