package nl.velocitymotors.car_booking_service.adapter.out.booking.mapper;

import nl.velocitymotors.car_booking_service.adapter.out.booking.CarBookingJpaEntity;
import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingSaved;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarBookingMapper {
    CarBookingSaved commandToDto(CarBookingConfirmCommand command);
    @Mapping(target = ".", source = "command")
    @Mapping(target = "bookingStatus", source = "bookingStatus")
    CarBookingJpaEntity commandToJpa(CarBookingConfirmCommand command, BookingStatusEnum bookingStatus);
    CarBookingSaved jpaToDto(CarBookingJpaEntity carBookingJpaEntity);
    CarBookingJpaEntity dtoToJpa(CarBookingSaved carBookingSaved);

    default String bookingStatusEnumToString(BookingStatusEnum bookingStatusEnum){
        return String.valueOf(bookingStatusEnum);
    }

}
