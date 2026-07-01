package nl.velocitymotors.car_booking_service.usecases;

import lombok.RequiredArgsConstructor;
import nl.velocitymotors.car_booking_service.domain.exceptions.BookingNotFoundException;
import nl.velocitymotors.car_booking_service.domain.model.Booking;
import nl.velocitymotors.car_booking_service.port.in.UpdateBookingPayedByBankTransferPort;
import nl.velocitymotors.car_booking_service.port.out.CarBookingPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateBookingPayedByBankTransferPaymentUseCase implements UpdateBookingPayedByBankTransferPort {

    private final CarBookingPort carBookingPort;

    @Override
    @Transactional
    public void execute(final String bookingId) {
        final Booking booking = carBookingPort.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("The provided booking ID was not found: " + bookingId));

        booking.confirmPayment();
        carBookingPort.save(booking);
    }
}
