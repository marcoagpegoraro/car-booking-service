package nl.velocitymotors.car_booking_service.adapter.out.payment.mapper;

import nl.velocitymotors.car_booking_service.adapter.out.payment.dto.PaymentServiceResponse;
import nl.velocitymotors.car_booking_service.domain.model.PaymentDetails;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentDetailsMapper {

    PaymentDetails responseToDetails(PaymentServiceResponse carBookingConfirmRequest);

}
