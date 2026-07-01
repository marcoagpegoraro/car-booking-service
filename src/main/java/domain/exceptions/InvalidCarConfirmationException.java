package domain.exceptions;

public class InvalidCarConfirmationException extends RuntimeException {
    public InvalidCarConfirmationException(String message) {
        super(message);
    }
}
