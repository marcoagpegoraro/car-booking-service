package nl.velocitymotors.car_booking_service.domain.model;

import java.time.OffsetDateTime;

public record CarBookingSaved(
        String id,
        String vehicleID,
        OffsetDateTime bookingStartDate,
        OffsetDateTime bookingEndDate,
        String customerName,
        String vehicleCategory,
        String paymentMode,
        String paymentReference,
        String bookingStatus
) {
}
