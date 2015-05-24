package hr.fer.zemris.parallelmachinesimulator.pramprocessor.statement;

import hr.fer.zemris.parallelmachinesimulator.exception.MemoryViolation;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.interpreter.PythonInterpreter;
import hr.fer.zemris.parallelmachinesimulator.model.MemoryModel;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.AbstractPRAMProcessor;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.BlockProperty;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.PRAMProcessorStatement;
import org.python.core.PyObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by antivo
 */
@Component
@Scope("prototype")
@PRAMProcessorStatement(keyword = Print.KEYWORD)
public class Print extends AbstractPRAMProcessor {
    public static final String KEYWORD = "print";

    private String expression;

    @Autowired
    private PythonInterpreter pythonInterpreter;

    @Override
    protected MemoryModel MEMORY_MODEL() {
        return MemoryModel.RAM;
    }

    private static String getExpression(String[] partsOfStatement, String line) throws SyntaxException {
        if(partsOfStatement.length == 0) {
            return "";
        }

        if(null == partsOfStatement || 2 != partsOfStatement.length || !partsOfStatement[0].equals("")
                || (partsOfStatement[1].trim().length() > 0 && ' ' != partsOfStatement[1].charAt(0))) {
            throw SyntaxException.invalidStatement(KEYWORD, line);
        }
        return partsOfStatement[1];
    }

    @Override
    protected void assign(String line) throws SyntaxException {
        String s[] = line.trim().split(KEYWORD);
        this.expression = getExpression(s, line);

    }

    @Override
    public BlockProperty getBlockProperty() {
        return BlockProperty.BODY;
    }

    private PyObject getValue() throws SyntaxException {
        try {
            return pythonInterpreter.eval(expression);
        } catch (Exception e) {
            throw SyntaxException.invalidExpression(expression);
        }
    }

    @Override
    protected void executeBlock() throws SyntaxException, MemoryViolation {
        if(expression.trim().equals("")) {
            System.out.println();
        } else {
            PyObject object = getValue();
            System.out.println(object);
        }
    }

    @Override
    protected Optional<String> getLHS() {
        return Optional.empty();
    }

    @Override
    protected Optional<String> getRHS() {
        return Optional.of(expression);
    }

    @Override
    protected Optional<String> getIL() {
        return Optional.empty();
    }
}
