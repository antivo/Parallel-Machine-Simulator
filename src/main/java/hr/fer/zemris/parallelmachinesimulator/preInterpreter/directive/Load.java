package hr.fer.zemris.parallelmachinesimulator.preInterpreter.directive;

import hr.fer.zemris.parallelmachinesimulator.ParallelMachineSimulator;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.preInterpreter.PreInterpreterDirective;
import hr.fer.zemris.parallelmachinesimulator.utils.FileUtils;
import hr.fer.zemris.parallelmachinesimulator.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by antivo
 */
@Component
public class Load implements PreInterpreterDirective {
    private static final String KEYWORD = ":load";

    @Autowired
    private ParallelMachineSimulator parallelMachineSimulator;

    private static String generateOffset(int offset) {
        StringBuffer outputBuffer = new StringBuffer(offset);
        for (int i = 0; i < offset; ++i){
            outputBuffer.append(" ");
        }
        return outputBuffer.toString();
    }

    private static void assertCommandParts(String[] parts) throws SyntaxException {
        if(2 != parts.length || !parts[0].equals(KEYWORD)) {
            throw new SyntaxException("Invalid use of keyword " + KEYWORD);
        }
    }

    @Override
    public boolean canProcess(String line) {
        line = line.trim();
        if(getKeyword().length() <= line.length()) {
            for(int i = 0; i <getKeyword().length(); ++i) {
                if(getKeyword().charAt(i) != line.charAt(i)) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean process(String line) throws SyntaxException {
        final int offset = StringUtils.calculateIndentation(line);
        line = line.trim();
        String[] parts = line.split(" ");
        assertCommandParts(parts);
        String filename = parts[1];
        try {
            List<String> lines = FileUtils.getInstance().loadScript(filename);
            lines = lines.stream()
                    .map(ss ->
                            generateOffset(offset) +
                                    (ss.length() > 1 && ss.charAt(ss.length()-1)=='\n'?ss.substring(0, ss.length() - 1):ss))
                    .collect(Collectors.toList());
            publish(lines);
        } catch(FileNotFoundException e) {
            throw new SyntaxException(e.getMessage());
        }
        return false;
    }

    private void publish(List<String> lines) {
        parallelMachineSimulator.pushLines(lines);
    }

    public String getKeyword() {
        return KEYWORD;
    }
}
