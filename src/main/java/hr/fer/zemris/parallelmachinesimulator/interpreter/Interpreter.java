package hr.fer.zemris.parallelmachinesimulator.interpreter;

import hr.fer.zemris.parallelmachinesimulator.exception.MemoryViolation;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;

/**
 * Created by antivo
 */
public interface Interpreter {
    /*
    return - true if expects more input
     */
    public boolean push(String line) throws SyntaxException, MemoryViolation;
}
