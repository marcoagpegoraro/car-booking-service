package nl.velocitymotors.car_booking_service.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.velocitymotors.car_booking_service.adapter.in.web.dto.CarBookingConfirmRequest;
import nl.velocitymotors.car_booking_service.adapter.in.web.dto.CarBookingConfirmResponse;
import nl.velocitymotors.car_booking_service.adapter.in.web.mapper.CarBookingConfirmMapper;
import nl.velocitymotors.car_booking_service.port.in.ConfirmCarBookingPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/car-booking")
@RequiredArgsConstructor
public class CarBookingController {

    private final ConfirmCarBookingPort confirmCarBooking;
    private final CarBookingConfirmMapper carBookingConfirmMapper;


    @PostMapping("/confirm")
    public ResponseEntity<CarBookingConfirmResponse> confirm(
            @RequestBody @Valid CarBookingConfirmRequest request
    ) {
        log.info("Confirming car booking for vehicle {} with payment mode {}", request.vehicleID(), request.paymentMode());

        final var command = carBookingConfirmMapper.requestToCommand(request);
        final var executed = confirmCarBooking.execute(command);

        log.info("Car booking {} processed with status {}", executed.bookingId(), executed.bookingStatus());
        return ResponseEntity.ok(carBookingConfirmMapper.executedToResponse(executed));
    }
}
