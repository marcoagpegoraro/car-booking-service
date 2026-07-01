package nl.velocitymotors.car_booking_service.usecases.strategy;

import nl.velocitymotors.car_booking_service.domain.model.Booking;
import org.springframework.stereotype.Service;

@Service("CASH")
public class PaymentCashStrategy implements PaymentStrategy {

    @Override
    public void apply(final Booking booking) {
        booking.confirmPayment();
    }
}
