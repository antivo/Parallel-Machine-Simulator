package hr.fer.zemris.parallelmachinesimulator.pramprocessor;

import hr.fer.zemris.parallelmachinesimulator.exception.MemoryViolation;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.expression.ExpressionReceiver;
import hr.fer.zemris.parallelmachinesimulator.model.MemoryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by antivo
 */
@Component
@Scope("prototype")
public abstract class AbstractPRAMProcessor implements PRAMProcessor {
    private int indentation;
    private MemoryModel memoryModel;
    private List<PRAMProcessor> subBlocks;

    @Autowired
    private ExpressionReceiver expressionReceiver;

    @PostConstruct
    private void initAbstractPRAMProcessor() {
        if(getBlockProperty() == BlockProperty.CREATOR) {
            subBlocks = new ArrayList<>();
        }
    }

    private void setCombinedMemoryModel(PRAMProcessor parent) {
        MemoryModel model = this.MEMORY_MODEL();
        MemoryModel parentModel = parent.getMemoryModel();
        if(parentModel != MemoryModel.RAM) {
            model = parentModel;
        }
        this.memoryModel = model;
    }

    protected abstract MemoryModel MEMORY_MODEL();

    protected abstract void assign(String line) throws SyntaxException;

    private void assign(String line, int calculatedIndentation) {
        setIndentation(calculatedIndentation);
        setMemoryModel(this.MEMORY_MODEL());
    }

    @Override
    public void assign(String line, int calculatedIndentation, PRAMProcessor parent) throws SyntaxException {
        if(parent != null) {
            parent.push(this, calculatedIndentation, line);
            setIndentation(calculatedIndentation);
            setCombinedMemoryModel(parent);
        } else {
            assign(line, calculatedIndentation);
        }
        assign(line);
    }

    private void assertSubBlockAlignment(int calculatedIndentation, String line) throws SyntaxException {
        if(!subBlocks.isEmpty()) {
            int subBlockAlignment = subBlocks.get(0).getIndentation();
            if(subBlockAlignment != calculatedIndentation) {
                throw new SyntaxException("Unexpected indentation. line: '" + line + "'");
            }
        }
    }

    private void assertBlockProperty(String line) throws SyntaxException {
        if(getBlockProperty() == BlockProperty.BODY) {
            throw new SyntaxException("Unexpected line. line '"+ line +"'");
        }
    }

    @Override
    public void push(PRAMProcessor pramProcessor, int calculatedIndentation, String line) throws SyntaxException {
        assertBlockProperty(line);
        assertSubBlockAlignment(calculatedIndentation, line);
        subBlocks.add(pramProcessor);
    }

    @Override
    public void execute() throws SyntaxException, MemoryViolation {
        Optional<String> il = getIL();
        if(il.isPresent() && isParallelContained()) {
            String ignoreLocation = il.get();
            expressionReceiver.receiveIgnoreLocation(ignoreLocation);
        }
        Optional<String> lhs = getLHS();
        if(lhs.isPresent() && isParallelContained()) {
            expressionReceiver.receiveLHS(lhs.get());
        }
        Optional<String> rhs = getRHS();
        if(rhs.isPresent() && isParallelContained()) {
            expressionReceiver.receiveRHS(rhs.get());
        }
        executeBlock();
    }

    protected abstract void executeBlock() throws SyntaxException, MemoryViolation;

    protected void executeSubBlocks() throws SyntaxException, MemoryViolation {
        if(null != subBlocks) {
            for(PRAMProcessor p : subBlocks) {
                p.execute();
            }
        }
    }

    @Override
    public MemoryModel getMemoryModel() {
        return memoryModel;
    }

    public void setMemoryModel(MemoryModel memoryModel) {
        this.memoryModel = memoryModel;
    }

    @Override
    public int getIndentation() {
        return indentation;
    }

    public void setIndentation(int indentation) {
        this.indentation = indentation;
    }

    protected abstract Optional<String> getLHS();
    protected abstract Optional<String> getRHS();
    protected abstract Optional<String> getIL();

    public boolean isParallelContained() {
        return getMemoryModel() != MemoryModel.RAM;
    }

    protected void cleanSubBlocks() {
        this.subBlocks = new ArrayList<>();
    }
}
