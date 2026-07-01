package domain.model;

import java.time.OffsetDateTime;

public record CarBookingSaved(
        Long id,
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
