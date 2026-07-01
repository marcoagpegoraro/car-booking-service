package nl.velocitymotors.car_booking_service.domain.model;

import nl.velocitymotors.car_booking_service.domain.enums.PaymentStatusEnum;

import java.time.OffsetDateTime;

public record PaymentDetails(
        OffsetDateTime lastUpdateDate,
        PaymentStatusEnum status) {
}
