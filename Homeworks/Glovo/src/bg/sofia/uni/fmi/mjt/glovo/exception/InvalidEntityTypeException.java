package bg.sofia.uni.fmi.mjt.glovo.exception;

public class InvalidEntityTypeException extends RuntimeException {
    public InvalidEntityTypeException(String message) {
        super(message);
    }

    public InvalidEntityTypeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}