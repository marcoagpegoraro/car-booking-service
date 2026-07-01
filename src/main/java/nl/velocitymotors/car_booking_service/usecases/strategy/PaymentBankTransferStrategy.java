package nl.velocitymotors.car_booking_service.usecases.strategy;

import lombok.extern.slf4j.Slf4j;
import nl.velocitymotors.car_booking_service.domain.model.Booking;
import org.springframework.stereotype.Service;

@Slf4j
@Service("BANK_TRANSFER")
public class PaymentBankTransferStrategy implements PaymentStrategy {

    @Override
    public void apply(final Booking booking) {
        log.info("Bank transfer payment received for the booking {}. No action is needed since booking is created with PENDING_PAYMENT.", booking.getId());
    }
}
