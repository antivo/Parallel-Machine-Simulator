package hr.fer.zemris.parallelmachinesimulator.preInterpreter;

import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;

/**
 * Created by antivo
 */
public interface PreInterpreterDirective {
    public boolean process(String line) throws SyntaxException ;
    public boolean canProcess(String line);
}
