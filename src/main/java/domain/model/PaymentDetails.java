package domain.model;

import domain.enums.PaymentStatusEnum;

import java.time.OffsetDateTime;

public record PaymentDetails(
        OffsetDateTime lastUpdateDate,
        PaymentStatusEnum status) {
}
