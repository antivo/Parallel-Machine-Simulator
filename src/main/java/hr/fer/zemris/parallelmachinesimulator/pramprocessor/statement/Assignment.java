package hr.fer.zemris.parallelmachinesimulator.pramprocessor.statement;

import hr.fer.zemris.parallelmachinesimulator.exception.MemoryViolation;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.interpreter.PythonInterpreter;
import hr.fer.zemris.parallelmachinesimulator.model.MemoryModel;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.AbstractPRAMProcessor;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.BlockProperty;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.PRAMProcessorStatement;
import hr.fer.zemris.parallelmachinesimulator.utils.StringUtils;
import org.python.core.PyObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by antivo
 */
@Component
@Scope("prototype")
@PRAMProcessorStatement(keyword = Assignment.KEYWORD)
public class Assignment extends AbstractPRAMProcessor {
    private String var;
    private String value;
    private String assignment;
    private Optional<String> il;

    @Autowired
    private PythonInterpreter pythonInterpreter;

    public static final String KEYWORD = "=";

    private static final char ASSIGNMENT = '=';
    private static final List<String> ASSIGNMENTS = new ArrayList<>(Arrays.asList(
            "+=", "-=", "*=", "/=", "//=", "%=", "**=", ">>=", "<<=", "&=", "^=", "|="));

    private String[] breakStatement(String statement) throws SyntaxException {
        for(int i = 1; i < statement.length(); ++i) {
            if(ASSIGNMENT == statement.charAt(i)) {
                final String test2L = statement.substring(i-1, i+1);
                if(ASSIGNMENTS.stream().anyMatch(a -> test2L.equals(a))) {
                    this.assignment = test2L;
                    return StringUtils.cut(statement, i-1, i);
                } else if(i>=2) {
                    final String test3L = statement.substring(i-2, i+1);
                    if(ASSIGNMENTS.stream().anyMatch(a -> test3L.equals(a))) {
                        this.assignment = test3L;
                        return StringUtils.cut(statement, i-2, i);
                    }
                }
                if(assignment == null) {
                    assignment = KEYWORD;
                    return StringUtils.cut(statement, i, i);
                }
            }
        }
        throw new SyntaxException("Impossible exception. Line containing assigment operator does not contain assigment operator");
    }

    private static void assertPartsOfStatement(String[] partsOfStatement, String line) throws SyntaxException {
        if(2 != partsOfStatement.length) {
            throw SyntaxException.invalidStatement("'" + KEYWORD + "'", line);
        }
    }

    private void determineIL() {
        this.il = Optional.empty();
        if(getMemoryModel() != MemoryModel.RAM) {
            try {
                pythonInterpreter.eval(var);
            } catch(Exception e) {
                this.il = Optional.of(var);
            }
        }
    }

    @Override
    protected void assign(String line) throws SyntaxException {
        String[] parts = breakStatement(line);
        assertPartsOfStatement(parts, line);

        this.var = parts[0].trim();
        this.value = parts[1].trim();
        determineIL();
    }

    private PyObject getValue() throws SyntaxException {
        try {
            if(assignment.length() > 1) {
                String infix = assignment.substring(0,assignment.length()-1);
                return pythonInterpreter.eval(var + infix +value);
            } else {
                return pythonInterpreter.eval(value);
            }
        } catch (Exception e) {
            throw SyntaxException.invalidExpression(value);
        }
    }

    @Override
    protected void executeBlock() throws SyntaxException, MemoryViolation {
        PyObject pyObject = getValue();
        try {
            pythonInterpreter.set(var, pyObject);
            if(pythonInterpreter.eval(var) == null) {
                throw new SyntaxException("Can not assign: '" + value + "' to: '" + var + "'");
            }
        } catch (Exception e){
            throw new SyntaxException("Can not assign: '" + value + "' to: '" + var + "'");
        }
    }

    @Override
    protected Optional<String> getLHS() {
        return Optional.of(var);
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
        return BlockProperty.BODY;
    }

    @Override
    protected MemoryModel MEMORY_MODEL() {
        return MemoryModel.RAM;
    }
}
