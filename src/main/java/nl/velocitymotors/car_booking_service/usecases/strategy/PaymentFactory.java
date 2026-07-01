package nl.velocitymotors.car_booking_service.usecases.strategy;

import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidPaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PaymentFactory {
    private final Map<String, PaymentStrategy> paymentStrategies;

    public PaymentStrategy get(final PaymentModeEnum paymentMode) {
        final var paymentStrategy = paymentStrategies.get(String.valueOf(paymentMode));
        if (Objects.isNull(paymentStrategy)) {
            throw new InvalidPaymentException("Unsupported payment strategy");
        }
        return paymentStrategy;
    }
}
