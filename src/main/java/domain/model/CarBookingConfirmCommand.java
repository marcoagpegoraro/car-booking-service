package domain.model;

import domain.enums.PaymentModeEnum;
import domain.enums.VehicleCategoryEnum;

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