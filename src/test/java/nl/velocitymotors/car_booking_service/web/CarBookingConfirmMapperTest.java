package nl.velocitymotors.car_booking_service.web;

import nl.velocitymotors.car_booking_service.adapter.in.web.dto.CarBookingConfirmRequest;
import nl.velocitymotors.car_booking_service.adapter.in.web.dto.CarBookingConfirmResponse;
import nl.velocitymotors.car_booking_service.adapter.in.web.dto.PaymentModeRequestEnum;
import nl.velocitymotors.car_booking_service.adapter.in.web.dto.VehicleCategoryRequestEnum;
import nl.velocitymotors.car_booking_service.adapter.in.web.mapper.CarBookingConfirmMapper;
import nl.velocitymotors.car_booking_service.adapter.in.web.mapper.CarBookingConfirmMapperImpl;
import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.enums.VehicleCategoryEnum;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingConfirmCommand;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingExecuted;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CarBookingConfirmMapperTest {

    private static final OffsetDateTime START = OffsetDateTime.parse("2026-09-10T10:00:00Z");
    private static final OffsetDateTime END = OffsetDateTime.parse("2026-09-12T10:00:00Z");

    private final CarBookingConfirmMapper mapper = new CarBookingConfirmMapperImpl();

    @ParameterizedTest
    @EnumSource(VehicleCategoryRequestEnum.class)
    void shouldMapRequestToCommandForEveryVehicleCategory(final VehicleCategoryRequestEnum vehicleCategory) {
        //given
        final var request = new CarBookingConfirmRequest("Marco", "VH-1", START, END,
                vehicleCategory, PaymentModeRequestEnum.CASH, "ref-1");

        //when
        final CarBookingConfirmCommand command = mapper.requestToCommand(request);

        //then
        assertEquals("Marco", command.customerName());
        assertEquals("VH-1", command.vehicleID());
        assertEquals(START, command.rentalStartDate());
        assertEquals(END, command.rentalEndDate());
        assertEquals("ref-1", command.paymentReference());
        assertEquals(VehicleCategoryEnum.valueOf(vehicleCategory.name()), command.vehicleCategory());
    }

    @ParameterizedTest
    @EnumSource(PaymentModeRequestEnum.class)
    void shouldMapRequestToCommandForEveryPaymentMode(final PaymentModeRequestEnum paymentMode) {
        //given
        final var request = new CarBookingConfirmRequest("Marco", "VH-1", START, END,
                VehicleCategoryRequestEnum.SUV, paymentMode, "ref-1");

        //when
        final CarBookingConfirmCommand command = mapper.requestToCommand(request);

        //then
        assertEquals(PaymentModeEnum.valueOf(paymentMode.name()), command.paymentMode());
    }

    @Test
    void shouldMapExecutedResultToResponse() {
        //given
        final var executed = new CarBookingExecuted("BKG0000001", BookingStatusEnum.CONFIRMED);

        //when
        final CarBookingConfirmResponse response = mapper.executedToResponse(executed);

        //then
        assertEquals("BKG0000001", response.bookingId());
        assertEquals("CONFIRMED", response.bookingStatus());
    }
}
