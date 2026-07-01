package nl.velocitymotors.car_booking_service.adapter.out.booking;

import nl.velocitymotors.car_booking_service.adapter.out.booking.mapper.CarBookingMapper;
import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.exceptions.BookingNotFoundException;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingSaved;
import nl.velocitymotors.car_booking_service.port.out.CarBookingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
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

    public void updateBookingPaymentStatus(Long bookingId, BookingStatusEnum bookingStatus){
        Optional<CarBookingJpaEntity> bookingOptional = carBookingJpaRepository.findById(bookingId);
        if(bookingOptional.isEmpty()){
            throw new BookingNotFoundException("The provided booking ID was not found: " + bookingId);
        }
        final var booking = bookingOptional.get();
        booking.setBookingStatus(String.valueOf(bookingStatus));
        carBookingJpaRepository.save(booking);
    }

    public List<CarBookingSaved> findBookingsStartingBefore(final PaymentModeEnum paymentMode,
                                                            final BookingStatusEnum status,
                                                            final OffsetDateTime rentalStartDateThreshold) {
        return carBookingJpaRepository
                .findByPaymentModeAndBookingStatusAndRentalStartDateLessThanEqual(
                        paymentMode.name(), status.name(), rentalStartDateThreshold)
                .stream()
                .map(carBookingMapper::jpaToDto)
                .toList();
    }
}
