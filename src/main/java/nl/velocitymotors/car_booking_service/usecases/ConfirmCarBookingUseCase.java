package nl.velocitymotors.car_booking_service.usecases;

import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingExecuted;
import nl.velocitymotors.car_booking_service.port.in.ConfirmCarBookingPort;
import nl.velocitymotors.car_booking_service.usecases.strategy.PaymentFactory;
import nl.velocitymotors.car_booking_service.usecases.strategy.PaymentStrategy;
import nl.velocitymotors.car_booking_service.usecases.validators.CarBookingConfirmValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConfirmCarBookingUseCase implements ConfirmCarBookingPort {

    /*
        It is a list of beans that implements the validator interface, should in the future
        the application require more validators, the only thing that the developer needs
        to do is to create another spring component that implements the interface.
     */
    private final List<CarBookingConfirmValidator> validators;
    private final PaymentFactory paymentFactory;

    @Override
    public CarBookingExecuted execute(CarBookingConfirmCommand command) {
        validators.forEach(validator -> validator.validate(command));
        PaymentStrategy paymentStrategy = paymentFactory.get(command.paymentMode());
        return paymentStrategy.execute(command);
    }
}
