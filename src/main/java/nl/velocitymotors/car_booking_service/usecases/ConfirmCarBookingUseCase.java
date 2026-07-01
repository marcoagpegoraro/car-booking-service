package nl.velocitymotors.car_booking_service.usecases;

import lombok.RequiredArgsConstructor;
import nl.velocitymotors.car_booking_service.domain.model.Booking;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingExecuted;
import nl.velocitymotors.car_booking_service.domain.model.RentalPeriod;
import nl.velocitymotors.car_booking_service.port.in.ConfirmCarBookingPort;
import nl.velocitymotors.car_booking_service.port.out.CarBookingPort;
import nl.velocitymotors.car_booking_service.usecases.strategy.PaymentFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConfirmCarBookingUseCase implements ConfirmCarBookingPort {

    private final PaymentFactory paymentFactory;
    private final CarBookingPort carBookingPort;

    @Override
    @Transactional
    public CarBookingExecuted execute(final CarBookingConfirmCommand command) {
        final Booking booking = Booking.request(
                command.customerName(),
                command.vehicleID(),
                new RentalPeriod(command.rentalStartDate(), command.rentalEndDate()),
                command.vehicleCategory(),
                command.paymentMode(),
                command.paymentReference());

        paymentFactory.get(command.paymentMode()).apply(booking);

        final Booking savedBooking = carBookingPort.save(booking);
        return new CarBookingExecuted(savedBooking.getId(), savedBooking.getStatus());
    }
}
