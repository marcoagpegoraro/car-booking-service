package nl.velocitymotors.car_booking_service.domain.model;

import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.enums.VehicleCategoryEnum;

import java.time.OffsetDateTime;

public record CarBookingConfirmCommand(
        String customerName,
        String vehicleID,
        OffsetDateTime rentalStartDate,
        OffsetDateTime rentalEndDate,
        VehicleCategoryEnum vehicleCategory,
        PaymentModeEnum paymentMode,
        String paymentReference
){
}