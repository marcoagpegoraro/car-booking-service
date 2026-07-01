package nl.velocitymotors.car_booking_service.adapter.in.web.dto;

public record CarBookingConfirmResponse(
        Long bookingId,
        String bookingStatus
){
}
