package hr.fer.zemris.parallelmachinesimulator.exception;

/**
 * Created by antivo
 */
public class SyntaxException extends Exception {
    public SyntaxException() {}


    public static SyntaxException invalidExpression(String expression) {
        return new SyntaxException("Not a valid expression: '" + expression +"'");
    }

    public static SyntaxException invalidStatement(String keyword, String line) {
        return new SyntaxException("Invalid " + keyword + "-statement. Line: '" + line + "'");
    }

    public static SyntaxException functionsCall(String rhs) {
        return new SyntaxException("Function calls are not allowed in PRAM model. In expression: '" + rhs + "'");
    }

    public SyntaxException(String message) {
        super(message);
    }

    public SyntaxException(Throwable cause) {
        super(cause);
    }

    public SyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public SyntaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
