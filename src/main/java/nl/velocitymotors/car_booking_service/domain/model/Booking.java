package nl.velocitymotors.car_booking_service.domain.model;

import lombok.Getter;
import nl.velocitymotors.car_booking_service.domain.enums.BookingStatusEnum;
import nl.velocitymotors.car_booking_service.domain.enums.PaymentModeEnum;
import nl.velocitymotors.car_booking_service.domain.enums.VehicleCategoryEnum;
import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidBookingStateException;
import nl.velocitymotors.car_booking_service.domain.exceptions.InvalidCarConfirmationException;

@Getter
public class Booking {

    private Long id;
    private final String customerName;
    private final String vehicleId;
    private final RentalPeriod rentalPeriod;
    private final VehicleCategoryEnum vehicleCategory;
    private final PaymentModeEnum paymentMode;
    private final String paymentReference;
    private BookingStatusEnum status;

    private Booking(final Long id, final String customerName, final String vehicleId, final RentalPeriod rentalPeriod,
                    final VehicleCategoryEnum vehicleCategory, final PaymentModeEnum paymentMode,
                    final String paymentReference, final BookingStatusEnum status) {
        this.id = id;
        this.customerName = customerName;
        this.vehicleId = vehicleId;
        this.rentalPeriod = rentalPeriod;
        this.vehicleCategory = vehicleCategory;
        this.paymentMode = paymentMode;
        this.paymentReference = paymentReference;
        this.status = status;
    }


    public static Booking request(final String customerName, final String vehicleId, final RentalPeriod rentalPeriod,
                                  final VehicleCategoryEnum vehicleCategory, final PaymentModeEnum paymentMode,
                                  final String paymentReference) {
        if (vehicleId == null || vehicleId.isBlank()) {
            throw new InvalidCarConfirmationException("The vehicle ID cannot be empty");
        }
        return new Booking(null, customerName, vehicleId, rentalPeriod, vehicleCategory, paymentMode,
                paymentReference, BookingStatusEnum.PENDING_PAYMENT);
    }

    public static Booking reconstitute(final Long id, final String customerName, final String vehicleId,
                                       final RentalPeriod rentalPeriod, final VehicleCategoryEnum vehicleCategory,
                                       final PaymentModeEnum paymentMode, final String paymentReference,
                                       final BookingStatusEnum status) {
        return new Booking(id, customerName, vehicleId, rentalPeriod, vehicleCategory, paymentMode, paymentReference, status);
    }

    public void confirmPayment() {
        if (BookingStatusEnum.CONFIRMED == status) {
            return;
        }
        if (BookingStatusEnum.PENDING_PAYMENT != status) {
            throw new InvalidBookingStateException("Cannot confirm booking " + id + " while it is " + status);
        }
        this.status = BookingStatusEnum.CONFIRMED;
    }

    public void cancel() {
        if (BookingStatusEnum.CANCELLED == status) {
            return;
        }
        if (BookingStatusEnum.PENDING_PAYMENT != status) {
            throw new InvalidBookingStateException("Cannot cancel booking " + id + " while it is " + status);
        }
        this.status = BookingStatusEnum.CANCELLED;
    }
}
