package hr.fer.zemris.parallelmachinesimulator.pramprocessor.statement;

import hr.fer.zemris.parallelmachinesimulator.exception.MemoryViolation;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.expression.ExpressionReceiver;
import hr.fer.zemris.parallelmachinesimulator.interpreter.PythonInterpreter;
import hr.fer.zemris.parallelmachinesimulator.model.MemoryModel;
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
@Scope("prototype")
@PRAMProcessorStatement(keyword = For.KEYWORD)
public class For extends AbstractPRAMProcessor {
    public static final String KEYWORD = "for ";

    private String var;
    private String value;
    private Optional<String> il;

    @Autowired
    private ExpressionReceiver expressionReceiver;

    @Autowired
    private PythonInterpreter pythonInterpreter;

    @Override
    protected MemoryModel MEMORY_MODEL() {
        return MemoryModel.RAM;
    }

    @Override
    protected void assign(String line) throws SyntaxException {
        String trimmedLine = line.trim();
        String[] partsOfStatement = trimmedLine.split(KEYWORD);
        if(partsOfStatement != null && partsOfStatement.length == 2 && partsOfStatement[0].equals("")) {
            String statement = partsOfStatement[1];
            if(':' == statement.charAt(statement.length()-1)) {
                statement = statement.substring(0, statement.length()-1);
                String[] parts = statement.split("in ");
                this.var = parts[0].trim();
                this.value = parts[1].trim();
                determineIL();
                return;
            }
        }
        throw SyntaxException.invalidStatement(KEYWORD, line);
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

    private void determineIL() {
        if(getMemoryModel() != MemoryModel.RAM && null == pythonInterpreter.get(var)) {
            this.il = Optional.of(var);
        } else {
            this.il = Optional.empty();
        }
    }

    @Override
    protected void executeBlock() throws SyntaxException, MemoryViolation {
        PySequence iterableObject = getValue();
        for (PyObject pyObject : iterableObject.asIterable()) {
            setVar(pyObject);
            executeSubBlocks();
        }
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
        return il;
    }

    @Override
    public BlockProperty getBlockProperty() {
        return BlockProperty.CREATOR;
    }
}
