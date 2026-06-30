package nl.velocitymotors.car_booking_service.adapter.in.web.dto;

public record CarBookingConfirmResponse(
        String bookingId,
        String bookingStatus
){
}
