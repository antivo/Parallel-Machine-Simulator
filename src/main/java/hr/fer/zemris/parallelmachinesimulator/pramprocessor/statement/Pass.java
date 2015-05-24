package hr.fer.zemris.parallelmachinesimulator.pramprocessor.statement;

import hr.fer.zemris.parallelmachinesimulator.exception.MemoryViolation;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.model.MemoryModel;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.AbstractPRAMProcessor;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.BlockProperty;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.PRAMProcessorStatement;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by antivo
 */
@Component
@PRAMProcessorStatement(keyword = Pass.KEYWORD)
public class Pass extends AbstractPRAMProcessor {
    public static final String KEYWORD = "pass";

    @Override
    protected MemoryModel MEMORY_MODEL() {
        return MemoryModel.RAM;
    }

    @Override
    protected void assign(String line) throws SyntaxException {
        if(!line.trim().equals(KEYWORD)) {
            throw SyntaxException.invalidStatement(KEYWORD, line);
        }
    }

    @Override
    protected void executeBlock() throws SyntaxException, MemoryViolation {
        // NOTHING TO DO
    }

    @Override
    protected Optional<String> getLHS() {
        return Optional.empty();
    }

    @Override
    protected Optional<String> getRHS() {
        return Optional.empty();
    }

    @Override
    protected Optional<String> getIL() {
        return Optional.empty();
    }

    @Override
    public BlockProperty getBlockProperty() {
        return BlockProperty.BODY;
    }
}
