package nl.velocitymotors.car_booking_service.adapter.out.payment;

import lombok.extern.slf4j.Slf4j;
import nl.velocitymotors.car_booking_service.domain.exceptions.IntegrationException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentServiceClientFallbackFactory implements FallbackFactory<PaymentServiceClient> {

    @Override
    public PaymentServiceClient create(final Throwable cause) {
        return request -> {
            log.error("Payment service call failed or circuit is open: {}", cause.getMessage());
            throw new IntegrationException("Payment service is currently unavailable.");
        };
    }
}
