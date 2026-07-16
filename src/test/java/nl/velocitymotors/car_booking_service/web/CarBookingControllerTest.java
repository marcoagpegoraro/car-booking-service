package nl.velocitymotors.car_booking_service.web;

import nl.velocitymotors.car_booking_service.adapter.in.web.CarBookingController;
import nl.velocitymotors.car_booking_service.adapter.in.web.mapper.CarBookingConfirmMapperImpl;
import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.exceptions.PaymentNotConfirmedException;
import nl.velocitymotors.car_booking_service.domain.model.CarBookingExecuted;
import nl.velocitymotors.car_booking_service.port.in.ConfirmCarBookingPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarBookingController.class)
@Import(CarBookingConfirmMapperImpl.class)
class CarBookingControllerTest {

    private static final String VALID_BODY = """
            {
              "customerName": "Marco",
              "vehicleID": "123",
              "rentalStartDate": "2026-09-10T10:00:00Z",
              "rentalEndDate": "2026-09-12T10:00:00Z",
              "vehicleCategory": "SUV",
              "paymentMode": "CASH",
              "paymentReference": "ref"
            }""";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConfirmCarBookingPort confirmCarBooking;

    @Test
    void shouldConfirmAValidBookingAndReturnTheResult() throws Exception {
        //given
        when(confirmCarBooking.execute(any()))
                .thenReturn(new CarBookingExecuted("BKG0000001", BookingStatusEnum.CONFIRMED));

        //when / then
        mockMvc.perform(post("/car-booking/confirm").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value("BKG0000001"))
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"));

        verify(confirmCarBooking).execute(any());
    }

    @Test
    void shouldReturn400WhenARequiredFieldIsMissing() throws Exception {
        //given
        final String bodyWithoutRequiredField = VALID_BODY.replace("\"customerName\": \"Marco\",", "");

        //when
        //then
        mockMvc.perform(post("/car-booking/confirm").contentType(MediaType.APPLICATION_JSON).content(bodyWithoutRequiredField))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("customerName")));

        verify(confirmCarBooking, never()).execute(any());
    }

    @Test
    void shouldReturn400WithTheAcceptedValuesWhenTheEnumIsInvalid() throws Exception {
        //given
        final String bodyWithInvalidPayment = VALID_BODY.replace("\"paymentMode\": \"CASH\"", "\"paymentMode\": \"IBAN\"");

        //when
        //then
        mockMvc.perform(post("/car-booking/confirm").contentType(MediaType.APPLICATION_JSON).content(bodyWithInvalidPayment))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("IBAN")))
                .andExpect(jsonPath("$.error", containsString("paymentMode")));
        verify(confirmCarBooking, never()).execute(any());
    }

    @Test
    void shouldReturnTheCachedBookingForARepeatedIdempotencyKey() throws Exception {
        //given
        when(confirmCarBooking.execute(any()))
                .thenReturn(new CarBookingExecuted("BKG0000002", BookingStatusEnum.CONFIRMED));

        //when
        //then
        mockMvc.perform(post("/car-booking/confirm")
                        .header("X-Idempotency-Key", "idem-key-A")
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value("BKG0000002"));

        mockMvc.perform(post("/car-booking/confirm")
                        .header("X-Idempotency-Key", "idem-key-A")
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value("BKG0000002"));

        verify(confirmCarBooking, times(1)).execute(any());
    }

    @Test
    void shouldMapADomainRejectionToItsHttpStatus() throws Exception {
        //given
        when(confirmCarBooking.execute(any()))
                .thenThrow(new PaymentNotConfirmedException("credit card payment not confirmed"));

        //when
        //then
        mockMvc.perform(post("/car-booking/confirm").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.error").value("credit card payment not confirmed"));
    }
}
