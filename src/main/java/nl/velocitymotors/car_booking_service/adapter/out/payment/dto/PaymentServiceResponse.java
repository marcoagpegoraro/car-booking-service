package nl.velocitymotors.car_booking_service.adapter.out.payment.dto;

import nl.velocitymotors.car_booking_service.domain.enums.PaymentStatusEnum;

import java.time.OffsetDateTime;

public record PaymentServiceResponse (
        OffsetDateTime lastUpdateDate,
        PaymentStatusEnum status
){
}
