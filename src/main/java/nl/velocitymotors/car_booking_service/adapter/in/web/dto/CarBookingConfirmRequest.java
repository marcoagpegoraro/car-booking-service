package nl.velocitymotors.car_booking_service.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record CarBookingConfirmRequest(
        @NotNull String customerName,
        @NotNull Integer vehicleID,
        @NotNull OffsetDateTime rentalStartDate,
        @NotNull OffsetDateTime rentalEndDate,
//        @NotNull VehicleCategoryEnum vehicleCategory,
//        @NotNull PaymentModeEnum paymentMode,
        String paymentReference
){
}
