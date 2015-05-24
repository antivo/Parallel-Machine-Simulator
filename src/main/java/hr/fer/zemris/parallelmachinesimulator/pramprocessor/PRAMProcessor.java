package hr.fer.zemris.parallelmachinesimulator.pramprocessor;

import hr.fer.zemris.parallelmachinesimulator.exception.MemoryViolation;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.model.MemoryModel;

/**
 * Created by antivo
 */
public interface PRAMProcessor {
    public void assign(String line, int calculatedIndentation, PRAMProcessor parent) throws SyntaxException;

    public void execute() throws SyntaxException, MemoryViolation;

    public void push(PRAMProcessor pramProcessor, int calculatedIndentation, String line) throws SyntaxException;

    public int getIndentation();

    public MemoryModel getMemoryModel();

    public BlockProperty getBlockProperty();
}
