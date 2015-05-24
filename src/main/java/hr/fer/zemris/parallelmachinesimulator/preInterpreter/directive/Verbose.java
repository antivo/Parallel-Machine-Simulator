package hr.fer.zemris.parallelmachinesimulator.preInterpreter.directive;

import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.output.VerboseComponent;
import hr.fer.zemris.parallelmachinesimulator.preInterpreter.PreInterpreterDirective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by antivo
 */
@Component
public class Verbose implements PreInterpreterDirective {
    private static final String KEYWORD = ":verbose";

    @Autowired
    private VerboseComponent verboseComponent;

    @Override
    public boolean process(String line) throws SyntaxException {
        line = line.trim();
        String[] parts = line.split(" ");
        assertCommandParts(parts);
        String value = parts[1].trim();
        boolean verbose = false;
        try {
            verbose = Boolean.parseBoolean(value);
            verboseComponent.setVerbosity(verbose);
        } catch(Exception e) {
            throw SyntaxException.invalidStatement("verbose", line);
        }
        return false;
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


    public String getKeyword() {
        return KEYWORD;
    }

    private static void assertCommandParts(String[] parts) throws SyntaxException {
        if(2 != parts.length || !parts[0].equals(KEYWORD)) {
            throw new SyntaxException("Invalid use of keyword " + KEYWORD);
        }
    }
}
