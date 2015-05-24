package hr.fer.zemris.parallelmachinesimulator.preInterpreter.directive;

import hr.fer.zemris.parallelmachinesimulator.ParallelMachineSimulator;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.interpreter.ActiveInterpreter;
import hr.fer.zemris.parallelmachinesimulator.preInterpreter.PreInterpreterDirective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by antivo on 5/14/15.
 */
@Component
public class Reset implements PreInterpreterDirective {
    private static final String KEYWORD = ":reset";

    @Autowired
    private ActiveInterpreter activeInterpreter;

    @Autowired
    private ParallelMachineSimulator parallelMachineSimulator;

    @Override
    public boolean process(String line) throws SyntaxException {
        parallelMachineSimulator.reset();
        System.out.println("Local namespace - reset");
        return false;
    }

    @Override
    public boolean canProcess(String line) {
        return (!activeInterpreter.isActive() && line.equals(KEYWORD));
    }

    public String getKeyword() {
        return KEYWORD;
    }
}
