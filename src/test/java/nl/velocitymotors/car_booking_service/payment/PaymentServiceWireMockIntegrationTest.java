package nl.velocitymotors.car_booking_service.payment;

import nl.velocitymotors.car_booking_service.adapter.out.payment.PaymentServiceAdapter;
import nl.velocitymotors.car_booking_service.adapter.out.payment.PaymentServiceClient;
import nl.velocitymotors.car_booking_service.adapter.out.payment.PaymentServiceClientFallbackFactory;
import nl.velocitymotors.car_booking_service.adapter.out.payment.mapper.PaymentDetailsMapperImpl;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentStatusEnum;
import nl.velocitymotors.car_booking_service.domain.exceptions.IntegrationException;
import nl.velocitymotors.car_booking_service.domain.model.PaymentDetails;
import nl.velocitymotors.car_booking_service.port.out.PaymentServicePort;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = PaymentServiceWireMockIntegrationTest.PaymentIntegrationTestApp.class)
class PaymentServiceWireMockIntegrationTest {

    private static final String PAYMENT_STATUS_PATH = "/host/credit-card-payment-api/payment-status";

    @RegisterExtension
    static final WireMockExtension PAYMENT_SERVICE = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void paymentServiceUrl(final DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.openfeign.client.config.payment-service-client.url",
                () -> PAYMENT_SERVICE.baseUrl() + "/host/credit-card-payment-api");
    }

    @Autowired
    private PaymentServicePort paymentServicePort;

    @Test
    void shouldMapAnApprovedPaymentAndSendTheReferenceInTheRequest() {
        //given
        PAYMENT_SERVICE.stubFor(post(urlPathEqualTo(PAYMENT_STATUS_PATH))
                .willReturn(okJson("{\"lastUpdateDate\":\"2026-07-01T10:00:00Z\",\"status\":\"APPROVED\"}")));

        //when
        final PaymentDetails details = paymentServicePort.getPaymentDetails("DL123456789");

        //then
        assertEquals(PaymentStatusEnum.APPROVED, details.status());
        PAYMENT_SERVICE.verify(postRequestedFor(urlPathEqualTo(PAYMENT_STATUS_PATH))
                .withRequestBody(matchingJsonPath("$.paymentReference", equalTo("DL123456789"))));
    }

    @Test
    void shouldMapARejectedPayment() {
        //given
        PAYMENT_SERVICE.stubFor(post(urlPathEqualTo(PAYMENT_STATUS_PATH))
                .willReturn(okJson("{\"lastUpdateDate\":\"2026-07-01T10:00:00Z\",\"status\":\"REJECTED\"}")));

        //when
        final PaymentDetails details = paymentServicePort.getPaymentDetails("DL987654321");

        //then
        assertEquals(PaymentStatusEnum.REJECTED, details.status());
    }

    @Test
    void shouldFailFastWhenThePaymentServiceReturnsAServerError() {
        //given
        PAYMENT_SERVICE.stubFor(post(urlPathEqualTo(PAYMENT_STATUS_PATH))
                .willReturn(aResponse().withStatus(500)));

        //when
        assertThrows(IntegrationException.class, () -> paymentServicePort.getPaymentDetails("DL123456789"));
    }

    @Configuration
    @EnableAutoConfiguration(excludeName = {
            "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
            "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
            "org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration"
    })
    @EnableFeignClients(clients = PaymentServiceClient.class)
    @Import({PaymentServiceAdapter.class, PaymentServiceClientFallbackFactory.class, PaymentDetailsMapperImpl.class})
    static class PaymentIntegrationTestApp {
    }
}
