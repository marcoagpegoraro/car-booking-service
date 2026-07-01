package nl.velocitymotors.car_booking_service.adapter.in.web.mapper;

import nl.velocitymotors.car_booking_service.adapter.in.web.dto.CarBookingConfirmRequest;
import nl.velocitymotors.car_booking_service.adapter.in.web.dto.CarBookingConfirmResponse;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingExecuted;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarBookingConfirmMapper {

    CarBookingConfirmCommand requestToCommand(CarBookingConfirmRequest carBookingConfirmRequest);
    CarBookingConfirmResponse executedToResponse(CarBookingExecuted carBookingExecuted);

}
