package nl.velocitymotors.car_booking_service.adapter.out.payment;

import nl.velocitymotors.car_booking_service.adapter.out.payment.dto.PaymentServiceRequest;
import nl.velocitymotors.car_booking_service.adapter.out.payment.dto.PaymentServiceResponse;
import nl.velocitymotors.car_booking_service.adapter.out.payment.mapper.PaymentDetailsMapper;
import nl.velocitymotors.car_booking_service.domain.exceptions.IntegrationException;
import nl.velocitymotors.car_booking_service.domain.model.PaymentDetails;
import nl.velocitymotors.car_booking_service.port.out.PaymentServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentServiceAdapter implements PaymentServicePort {

    private final PaymentServiceClient paymentServiceClient;
    private final PaymentDetailsMapper paymentDetailsMapper;

    public PaymentDetails getPaymentDetails(final String paymentId){
        final var request = new PaymentServiceRequest(paymentId);
        ResponseEntity<PaymentServiceResponse> response = paymentServiceClient.getPaymentDetails(request);

        if(response.getStatusCode().is2xxSuccessful()){
            return paymentDetailsMapper.responseToDetails(response.getBody());
        }

        throw new IntegrationException("Payment service currently unavailable. Reason: " + response.getBody());
    }
}
