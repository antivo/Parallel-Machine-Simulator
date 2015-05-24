package hr.fer.zemris.parallelmachinesimulator.pramprocessor.statement;

import hr.fer.zemris.parallelmachinesimulator.exception.MemoryViolation;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.expression.ExpressionReceiver;
import hr.fer.zemris.parallelmachinesimulator.interpreter.PythonInterpreter;
import hr.fer.zemris.parallelmachinesimulator.memory.JointMemory;
import hr.fer.zemris.parallelmachinesimulator.model.ActiveMemoryModel;
import hr.fer.zemris.parallelmachinesimulator.model.MemoryModel;
import hr.fer.zemris.parallelmachinesimulator.output.VerboseComponent;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.AbstractPRAMProcessor;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.BlockProperty;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.PRAMProcessorStatement;
import org.python.core.PyObject;
import org.python.core.PySequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by antivo
 */
@Component
@Scope("singleton")
@PRAMProcessorStatement(keyword = Parallel.KEYWORD)
public class Parallel extends AbstractPRAMProcessor {
    public static final String KEYWORD = "parallel ";

    private boolean active = false;

    private String var;
    private String value;

    @Autowired
    private PythonInterpreter pythonInterpreter;

    @Autowired
    private ActiveMemoryModel activeMemoryModel;

    @Autowired
    private ExpressionReceiver expressionReceiver;

    @Autowired
    private JointMemory jointMemory;

    @Autowired
    private VerboseComponent verboseComponent;

    @Override
    protected MemoryModel MEMORY_MODEL() {
        return activeMemoryModel.getMemoryModel();
    }

    @Override
    public BlockProperty getBlockProperty() {
        return BlockProperty.CREATOR;
    }

    @Override
    protected Optional<String> getLHS() {
        return Optional.empty();
    }

    @Override
    protected Optional<String> getRHS() {
        return Optional.of(value);
    }

    @Override
    protected Optional<String> getIL() {
        return Optional.of(var);
    }

    @Override
    protected void assign(String line) throws SyntaxException {
        if(!active) {
            active = true;
            String trimmedLine = line.trim();
            String[] partsOfStatement = trimmedLine.split(KEYWORD);
            if(partsOfStatement != null && partsOfStatement.length == 2) {
                if(partsOfStatement[0].equals("")) {
                    String statement = partsOfStatement[1];
                    if(':' == statement.charAt(statement.length()-1)) {
                        statement = statement.substring(0, statement.length()-1);
                        String[] parts = statement.split("in ");
                        this.var = parts[0].trim();
                        this.value = parts[1].trim();
                        return;
                    }
                }
            }
            throw SyntaxException.invalidStatement(KEYWORD, line);
        } else {
            throw new SyntaxException("Parallel block can not be contained in parallel block. Line: '" + line + "'");
        }
    }

    private PySequence getValue() throws SyntaxException {
        try {
            return (PySequence) pythonInterpreter.eval(value);
        } catch (Exception e) {
            throw new SyntaxException("Value '" + value + "' can not be used for iteration in for loop.");
        }
    }

    private void setVar(PyObject pyObject) throws SyntaxException {
        try {
            pythonInterpreter.set(var, pyObject);
        } catch (Exception e){
            throw new SyntaxException("Can not assign: '" + value + "' to: '" + var + "'");
        }
    }

    @Override
    protected void executeBlock() throws SyntaxException, MemoryViolation {
        active = false;
        PySequence iterableObject = getValue();
        for (PyObject pyObject : iterableObject.asIterable()) {
            expressionReceiver.receiveIgnoreLocation(var);
            verboseComponent.info("---------------------Node with " + var + " = " + pyObject);
            setVar(pyObject);
            executeSubBlocks();
            expressionReceiver.nextNode();
        }
        jointMemory.reset();
    }

    public void clear() {
        cleanSubBlocks();
        active = false;
    }
}
