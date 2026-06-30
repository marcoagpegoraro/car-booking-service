package nl.velocitymotors.car_booking_service.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.velocitymotors.car_booking_service.adapter.in.web.dto.CarBookingConfirmRequest;
import nl.velocitymotors.car_booking_service.adapter.in.web.dto.CarBookingConfirmResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/car-booking")
@RequiredArgsConstructor
public class CarBookingController {


    @PostMapping("/confirm")
    public ResponseEntity<CarBookingConfirmResponse> confirm(
            @RequestBody @Valid CarBookingConfirmRequest request
    ) {
        return ResponseEntity.ok(null);
    }
}
