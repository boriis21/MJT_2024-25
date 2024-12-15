package bg.sofia.uni.fmi.mjt.glovo.exception;

public class LocationOutOfMapException extends RuntimeException {
    public LocationOutOfMapException(String message) {
        super(message);
    }

    public LocationOutOfMapException(String message, Throwable throwable) {
        super(message, throwable);
    }
}