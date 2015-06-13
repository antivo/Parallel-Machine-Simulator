package hr.fer.zemris.parallelmachinesimulator.interpreter;

import hr.fer.zemris.parallelmachinesimulator.exception.MemoryViolation;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.BlockProperty;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.PRAMProcessor;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.PRAMProcessorFactory;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.statement.Parallel;
import hr.fer.zemris.parallelmachinesimulator.utils.StringUtils;
import org.python.core.PyObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Stack;

/**
 * Created by antivo
 */
@Component
public class PRAMInterpreter implements Interpreter {
    private Stack<PRAMProcessor> processors = new Stack<>();

    private boolean clean = true;
    private PyObject pythonInterpreterState = null;

    @Autowired
    private Parallel parallel;

    @Autowired
    private PythonInterpreter pythonInterpreter;

    @Autowired
    private PRAMProcessorFactory pramProcessorFactory;

    private static void assertPRAMProcessClosing(PRAMProcessor pramProcessor) throws SyntaxException {
        if(pramProcessor.getBlockProperty() != BlockProperty.BODY) {
            throw new SyntaxException("Unexpected ending of block");
        }
    }

    private void memorizeState() {
        if(clean) {
            pythonInterpreterState = pythonInterpreter.getLocals();
            clean = false;
        }
    }

    private void executeStack() throws SyntaxException, MemoryViolation {
        if(!processors.empty()) {
            assertPRAMProcessClosing(processors.peek());
            processors.get(0).execute();
            processors.clear();
        }
    }

    private void push(String line, int calculatedIndentation) throws SyntaxException {
        PRAMProcessor parent = null;
        if(!processors.empty()) {
            boolean top = true;
            while(!processors.empty() && calculatedIndentation <= processors.peek().getIndentation()) {
                if(top) {
                    assertPRAMProcessClosing(processors.peek());
                    top = false;
                }
                processors.pop();
            }
            parent = processors.peek();
        }
        processors.push(pramProcessorFactory.createPRAMProcessor(line, calculatedIndentation, parent));
    }

    @Override
    public boolean push(String line) throws SyntaxException, MemoryViolation {
        memorizeState();
        // handle empty lines
        line = line.replaceAll("[\r\n]+$", "");
        if (line.equals("") || line.equals(System.lineSeparator())) {
            executeStack();
            return false;
        } else if(line.trim().equals("")  && line.length() > 0) {
            return true;
        }
        // handle non empty lines
        int indentation = StringUtils.calculateIndentation(line);
        if(processors.empty()) {
            assertZeroIndentation(indentation, line);
        } else {
            assertNonZeroIndentation(indentation, line);
        }
        push(line, indentation);
        // handle one liners
        if(processors.size() == 1 && processors.peek().getBlockProperty() == BlockProperty.BODY) {
            executeStack();
        }
        // expect more if there are open blocks
        return 0 != processors.size();
    }

    private static void assertZeroIndentation(int calculatedIndentation, String line) throws SyntaxException {
        if(0 != calculatedIndentation) {
            throw new SyntaxException("IndentationError: unexpected indent '" + line + "'");
        }
    }

    private static void assertNonZeroIndentation(int calculatedIndentation, String line) throws SyntaxException {
        if(0 == calculatedIndentation) {
            throw new SyntaxException("IndentationError: unexpected indent '" + line + "'");
        }
    }

    public void clear() {
        processors.clear();
        pythonInterpreter.setLocalsTo(pythonInterpreterState);
        pythonInterpreterState = null;
        clean = true;
        parallel.clear(); // clear parallel processorpp
    }
}
