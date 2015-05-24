package hr.fer.zemris.parallelmachinesimulator.exception;

/**
 * Created by antivo
 */
public class MemoryViolation extends Exception {
    public MemoryViolation() {}

    public MemoryViolation(String message) {
        super(message);
    }

    public MemoryViolation(Throwable cause) {
        super(cause);
    }

    public MemoryViolation(String message, Throwable cause) {
        super(message, cause);
    }

    public MemoryViolation(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
