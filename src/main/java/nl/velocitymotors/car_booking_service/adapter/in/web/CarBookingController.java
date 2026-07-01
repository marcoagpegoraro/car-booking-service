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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/car-booking")
@RequiredArgsConstructor
public class CarBookingController {

    private final ConfirmCarBookingPort confirmCarBooking;
    private final CarBookingConfirmMapper carBookingConfirmMapper;

    // Idempotency store: if a client sends the same idempotency key twice (e.g. it
    // timed out and retried), we return the first response instead of creating a
    // second booking.
    //
    // In a real production application, this would be a persistence store (e.g. Redis)
    // that all the different microservice instances could reach. Right now it is only
    // local per instance and the cache is lost if the application restarts.
    //
    private final Map<String, CarBookingConfirmResponse> processedRequests = new ConcurrentHashMap<>();

    @PostMapping("/confirm")
    public ResponseEntity<CarBookingConfirmResponse> confirm(
            @RequestHeader(value = "X-Idempotency-Key", required = false) final String idempotencyKey,
            @RequestBody @Valid final CarBookingConfirmRequest request
    ) {
        if (idempotencyKey != null && processedRequests.containsKey(idempotencyKey)) {
            log.info("Returning the existing booking for idempotency key {}", idempotencyKey);
            return ResponseEntity.ok(processedRequests.get(idempotencyKey));
        }

        log.info("Confirming car booking for vehicle {} with payment mode {}", request.vehicleID(), request.paymentMode());

        final var command = carBookingConfirmMapper.requestToCommand(request);
        final var executed = confirmCarBooking.execute(command);
        final var response = carBookingConfirmMapper.executedToResponse(executed);

        if (idempotencyKey != null) {
            processedRequests.put(idempotencyKey, response);
        }

        log.info("Car booking {} processed with status {}", executed.bookingId(), executed.bookingStatus());
        return ResponseEntity.ok(response);
    }
}
