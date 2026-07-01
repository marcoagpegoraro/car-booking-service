package nl.velocitymotors.car_booking_service.adapter.in.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.velocitymotors.car_booking_service.port.in.CancelExpiredBankTransferBookingsPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankTransferBookingCancellationScheduler {

    private final CancelExpiredBankTransferBookingsPort cancelExpiredBankTransferBookingsPort;

    @Scheduled(cron = "${booking.cancellation.cron}")
    public void cancelExpiredBankTransferBookings() {
        log.info("Running auto cancellation of expired bank transfer bookings");
        final int cancelled = cancelExpiredBankTransferBookingsPort.execute();
        log.info("Auto cancellation finished: {} booking(s) cancelled", cancelled);
    }
}
