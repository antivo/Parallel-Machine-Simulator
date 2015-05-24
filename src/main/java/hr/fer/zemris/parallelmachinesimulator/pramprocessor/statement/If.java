package hr.fer.zemris.parallelmachinesimulator.pramprocessor.statement;

import hr.fer.zemris.parallelmachinesimulator.exception.MemoryViolation;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.interpreter.PythonInterpreter;
import hr.fer.zemris.parallelmachinesimulator.model.MemoryModel;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.AbstractPRAMProcessor;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.BlockProperty;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.PRAMProcessorStatement;
import org.python.core.PyBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by antivo
 */
@Component
@Scope("prototype")
@PRAMProcessorStatement(keyword = If.KEYWORD)
public class If extends AbstractPRAMProcessor {
    public static final String KEYWORD = "if";

    private String expression;

    @Autowired
    private PythonInterpreter pythonInterpreter;

    @Override
    protected MemoryModel MEMORY_MODEL() {
        return MemoryModel.RAM;
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
        return Optional.of(expression);
    }

    @Override
    protected Optional<String> getIL() {
        return Optional.empty();
    }

    private static String extractExpression(String line) throws SyntaxException {
        String statement = line.trim();
        if(statement.length() > KEYWORD.length() + 2) {
            String[] partsOfStatement = statement.split(KEYWORD);
            if(2 == partsOfStatement.length && partsOfStatement[0].equals("")) {
                statement = partsOfStatement[1];
                if(':' == statement.charAt(statement.length()-1)) {
                    return statement.substring(0, statement.length() - 1).trim();
                }
            }
        }
        throw SyntaxException.invalidStatement(KEYWORD,line);
    }

    @Override
    protected void assign(String line) throws SyntaxException {
        this.expression = extractExpression(line);
    }

    private boolean evaluate() throws SyntaxException {
        try {
            return ((PyBoolean) pythonInterpreter.eval(expression)).getBooleanValue();
        } catch (Exception e) {
            throw SyntaxException.invalidExpression(expression);
        }
    }

    @Override
    protected void executeBlock() throws SyntaxException, MemoryViolation {
        if(evaluate()) {
            executeSubBlocks();
        }
    }
}
