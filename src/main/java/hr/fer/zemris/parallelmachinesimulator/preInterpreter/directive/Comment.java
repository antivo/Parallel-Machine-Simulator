package hr.fer.zemris.parallelmachinesimulator.preInterpreter.directive;

import hr.fer.zemris.parallelmachinesimulator.ParallelMachineSimulator;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.preInterpreter.PreInterpreterDirective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by antivo
 */
@Component
public class Comment implements PreInterpreterDirective {
    private static final String KEYWORD = "#";

    @Autowired
    private ParallelMachineSimulator parallelMachineSimulator;

    @Override
    public boolean process(String line) throws SyntaxException {
        int position = line.indexOf(KEYWORD);
        String extractedLine = line.substring(0, position);
        if(!extractedLine.trim().equals("")) {
            parallelMachineSimulator.pushLine(extractedLine);
        }
        return true;
    }

    @Override
    public boolean canProcess(String line) {
        return line.contains(KEYWORD);
    }

    public String getKeyword() {
        return KEYWORD;
    }
}
