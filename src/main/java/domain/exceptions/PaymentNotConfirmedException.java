package domain.exceptions;

public class PaymentNotConfirmedException extends RuntimeException {
    public PaymentNotConfirmedException(String message) {
        super(message);
    }
}
