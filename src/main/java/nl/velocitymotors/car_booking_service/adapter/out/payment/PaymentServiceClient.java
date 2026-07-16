package nl.velocitymotors.car_booking_service.adapter.out.payment;

import nl.velocitymotors.car_booking_service.adapter.out.payment.dto.PaymentServiceRequest;
import nl.velocitymotors.car_booking_service.adapter.out.payment.dto.PaymentServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service-client", fallbackFactory = PaymentServiceClientFallbackFactory.class)
public interface PaymentServiceClient {

    @PostMapping("/payment-status")
    ResponseEntity<PaymentServiceResponse> getPaymentDetails(@RequestBody PaymentServiceRequest request);
}
