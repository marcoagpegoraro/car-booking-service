package nl.velocitymotors.car_booking_service.adapter.out.booking;

import nl.velocitymotors.car_booking_service.adapter.out.booking.mapper.CarBookingMapper;
import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.exceptions.BookingNotFoundException;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingSaved;
import nl.velocitymotors.car_booking_service.port.out.CarBookingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CarBookingAdapter implements CarBookingPort {

    private final CarBookingJpaRepository carBookingJpaRepository;
    private final CarBookingMapper carBookingMapper;

    public CarBookingSaved saveBooking(CarBookingConfirmCommand command, BookingStatusEnum bookingStatus){
        CarBookingJpaEntity carBookingJpaEntity = carBookingMapper.commandToJpa(command, bookingStatus);
        CarBookingJpaEntity savedCarBookingEntity = carBookingJpaRepository.save(carBookingJpaEntity);
        return carBookingMapper.jpaToDto(savedCarBookingEntity);
    }

    public void updateBookingPaymentStatus(String reservationId, BookingStatusEnum reservationStatus){
        Optional<CarBookingJpaEntity> reservationOptional = carBookingJpaRepository.findById(reservationId);
        if(reservationOptional.isEmpty()){
            throw new BookingNotFoundException("The provided booking ID was not found: " + reservationId);
        }
        final var reservation = reservationOptional.get();
        reservation.setBookingStatus(String.valueOf(reservationStatus));
        carBookingJpaRepository.save(reservation);
    }
}
