package hr.fer.zemris.parallelmachinesimulator.interpreter;

import hr.fer.zemris.parallelmachinesimulator.exception.MemoryViolation;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.preInterpreter.PreInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by antivo
 */
@Component
public class ActiveInterpreter {
    private Interpreter interpreter;

    @Autowired
    private InterpreterFactory interpreterFactory;

    @Autowired
    private PreInterpreter preInterpreter;

    public boolean isActive() {
        return null != interpreter;
    }

    public boolean interpret(String line) throws SyntaxException, MemoryViolation {
        if(null == interpreter) {
            interpreter = interpreterFactory.createInterpreter();
        }
        boolean expectingMore = interpreter.push(line);
        disposeOfInterpreter(expectingMore);
        return expectingMore;
    }

    public boolean push(String line) throws SyntaxException, MemoryViolation {
        if(preInterpreter.canProcess(line)) {
            return preInterpreter.process(line);
        }
        return interpret(line);
    }

    private void disposeOfInterpreter(boolean expectingMore) {
        if(!expectingMore) {
            cleanInterpreter();
        }
    }

    public void cleanInterpreter() {
        if(interpreter instanceof  PRAMInterpreter) {
            ((PRAMInterpreter) interpreter).clear();
        }
        interpreter = null;
    }
}
