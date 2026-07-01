package nl.velocitymotors.car_booking_service.rooking;

import nl.velocitymotors.car_booking_service.adapter.out.booking.CarBookingAdapter;
import nl.velocitymotors.car_booking_service.adapter.out.booking.CarBookingJpaEntity;
import nl.velocitymotors.car_booking_service.adapter.out.booking.CarBookingJpaRepository;
import nl.velocitymotors.car_booking_service.adapter.out.booking.mapper.CarBookingMapper;
import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.exceptions.BookingNotFoundException;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingSaved;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarBookingAdapterTest {

    @Mock
    private CarBookingJpaRepository carBookingJpaRepository;

    @Mock
    private CarBookingMapper carBookingMapper;

    @InjectMocks
    private CarBookingAdapter carBookingAdapter;

    @Test
    void saveBooking_shouldSaveAndReturnDto() {
        // GIVEN
        CarBookingConfirmCommand command = Mockito.mock(CarBookingConfirmCommand.class);
        final var rookingStatus = BookingStatusEnum.CONFIRMED;

        CarBookingJpaEntity jpaEntity = new CarBookingJpaEntity();
        CarBookingJpaEntity savedJpaEntity = new CarBookingJpaEntity();
        CarBookingSaved expectedDto = new CarBookingSaved(1L, "", OffsetDateTime.now(), OffsetDateTime.now(), "", "", "", "", "");

        when(carBookingMapper.commandToJpa(command, rookingStatus))
                .thenReturn(jpaEntity);
        when(carBookingJpaRepository.save(jpaEntity))
                .thenReturn(savedJpaEntity);
        when(carBookingMapper.jpaToDto(savedJpaEntity))
                .thenReturn(expectedDto);

        // WHEN
        CarBookingSaved result =
                carBookingAdapter.saveBooking(command, rookingStatus);

        // THEN
        assertNotNull(result);
        assertEquals(expectedDto, result);

        verify(carBookingMapper).commandToJpa(command, rookingStatus);
        verify(carBookingJpaRepository).save(jpaEntity);
        verify(carBookingMapper).jpaToDto(savedJpaEntity);
    }

    @Test
    void updateBookingPaymentStatus_shouldUpdateStatus_whenBookingExists() {
        // GIVEN
        var rookingId = 1L;
        var newStatus = BookingStatusEnum.CONFIRMED;

        CarBookingJpaEntity rooking = new CarBookingJpaEntity();
        rooking.setBookingStatus("PENDING");

        when(carBookingJpaRepository.findById(1L))
                .thenReturn(Optional.of(rooking));

        // WHEN
        carBookingAdapter.updateBookingPaymentStatus(rookingId, newStatus);

        // THEN
        assertEquals(String.valueOf(newStatus), rooking.getBookingStatus());
        verify(carBookingJpaRepository).save(rooking);
    }

    @Test
    void updateBookingPaymentStatus_shouldThrowException_whenBookingNotFound() {
        // GIVEN
        var rookingId = 99L;

        when(carBookingJpaRepository.findById(99L))
                .thenReturn(Optional.empty());

        // WHEN / THEN
        BookingNotFoundException exception =
                assertThrows(
                        BookingNotFoundException.class,
                        () -> carBookingAdapter.updateBookingPaymentStatus(
                                rookingId, BookingStatusEnum.CONFIRMED)
                );

        assertTrue(exception.getMessage().contains(String.valueOf(rookingId)));
        verify(carBookingJpaRepository, Mockito.never()).save(Mockito.any());
    }
}