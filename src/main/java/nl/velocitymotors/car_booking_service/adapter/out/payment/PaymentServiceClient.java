package nl.velocitymotors.car_booking_service.adapter.out.payment;

import nl.velocitymotors.car_booking_service.adapter.out.payment.dto.PaymentServiceRequest;
import nl.velocitymotors.car_booking_service.adapter.out.payment.dto.PaymentServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "payment-service-client")
public interface PaymentServiceClient {

    @RequestMapping("/payment-status")
    ResponseEntity<PaymentServiceResponse> getPaymentDetails(@RequestBody PaymentServiceRequest request);
}
