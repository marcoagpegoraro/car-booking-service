package nl.velocitymotors.car_booking_service.port.in;

public interface UpdateBookingPayedByBankTransferPort {

    void execute(Long bookingId);

}
