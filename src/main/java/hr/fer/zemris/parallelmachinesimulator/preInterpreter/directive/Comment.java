package hr.fer.zemris.parallelmachinesimulator.preInterpreter.directive;

import hr.fer.zemris.parallelmachinesimulator.ParallelMachineSimulator;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.preInterpreter.PreInterpreterDirective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by antivo
 */
@Component
public class Comment implements PreInterpreterDirective {
    private static final String KEYWORD = "#";

    @Autowired
    private ParallelMachineSimulator parallelMachineSimulator;

    public Optional<String> removeComment(String line) {
        if(canProcess(line)) {
        int position = line.indexOf(KEYWORD);
        String extractedLine = line.substring(0, position);
            if(!extractedLine.trim().equals("")) {
                return Optional.of(extractedLine);
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(line);
    }

    @Override
    public boolean process(String line) throws SyntaxException {
        Optional<String> extractedLine = removeComment(line);
        if(extractedLine.isPresent()) {
            parallelMachineSimulator.pushLine(extractedLine.get());
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
