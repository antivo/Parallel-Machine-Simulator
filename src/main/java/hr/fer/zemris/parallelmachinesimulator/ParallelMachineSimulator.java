package hr.fer.zemris.parallelmachinesimulator;

import hr.fer.zemris.parallelmachinesimulator.constants.ConsoleConstants;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.interpreter.ActiveInterpreter;
import hr.fer.zemris.parallelmachinesimulator.interpreter.PythonInterpreter;
import hr.fer.zemris.parallelmachinesimulator.utils.FileUtils;
import hr.fer.zemris.parallelmachinesimulator.utils.StringUtils;
import org.python.core.Py;
import org.python.core.PyString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by antivo
 */
@Component
public class ParallelMachineSimulator implements Runnable {
    private static final String SCAN_FUNCTION_SOURCE = "src/main/resources/scan.py";

    @Autowired
    private ActiveInterpreter activeInterpreter;

    @Autowired
    private PythonInterpreter pythonInterpreter;

    private final List<String> lines = Collections.synchronizedList(new LinkedList<>());

    @PostConstruct
    public void reset() {
        pythonInterpreter.setLocalsTo(Py.newStringMap());
        try {
            List<String> xss = FileUtils.readScript(SCAN_FUNCTION_SOURCE);
            String source = StringUtils.concatenate(xss);
            pythonInterpreter.exec(source);
        } catch (Exception e) {
            System.out.println("!! SCAN function is unavailable");
        }
    }

    private static void assertLine(String line) throws SyntaxException {
        if(line.contains("\t")) {
            throw new SyntaxException("Tabs are not allowed. Line '" + line + "' contains tabs.");
        }
    }

    private String requestCommand(PyString prompt, List<String> lines) {
        String line;
        if(lines.isEmpty()) {
            line = pythonInterpreter.raw_input(prompt);
        } else {
            line = lines.get(0);
            lines.remove(0);
            System.out.println(prompt.toString() + line);
        }
        return line;
    }

    @Override
    public void run() {
        printBanner();
        invokeTraceBack();
        for(boolean more = false;;) {
            PyString prompt = more ? ConsoleConstants.EXPECTING_INPUT : ConsoleConstants.READY_FOR_INPUT;
            String line = requestCommand(prompt, lines);
            try {
                assertLine(line);
                more = activeInterpreter.push(line);
            } catch(Exception e) {
                activeInterpreter.cleanInterpreter();
                pythonInterpreter.writeErr(e.getMessage() + "\n");
                more = false;
            }
        }
    }

    public void pushLines(List<String> lines) {
        this.lines.addAll(lines);
    }

    public void pushLine(String line) {
        this.lines.add(line);
    }

    private void printBanner() {
        System.out.print("Parallel Machine Simulator v1.0");
        System.out.println(" - please visit https://github.com/antivo/Parallel-Machine-Simulator");
    }

    private void invokeTraceBack() {
        pythonInterpreter.exec("2");
    }
}
