package hr.fer.zemris.parallelmachinesimulator.interpreter;

import org.python.core.PyBoolean;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.InteractiveConsole;
import org.springframework.stereotype.Component;

/**
 * Created by antivo
 */
@Component
public class JythonInterpreter implements PythonInterpreter {
    private InteractiveConsole interactiveConsole = new InteractiveConsole();

    @Override
    public String raw_input(PyString prompt) {
        return interactiveConsole.raw_input(prompt);
    }

    @Override
    public void writeErr(String data) {
        interactiveConsole.write(data);
    }

    @Override
    public void exec(String toExecute) {
        interactiveConsole.exec(toExecute);
    }

    @Override
    public PyObject eval(String toEvaluate) {
        return interactiveConsole.eval(toEvaluate);
    }

    @Override
    public boolean push(String line) {
        return interactiveConsole.push(line);
    }

    @Override
    public boolean is(String x, String y) {
        PyBoolean isSame = (PyBoolean) interactiveConsole.eval(x + " is " + y);
        return isSame.getBooleanValue();
    }

    public static String[] PRIMITIVE_MEMORY_LOCATIONS = {"bool", "int", "float", "complex", "str"};

    @Override
    public boolean isPrimitiveLocation(String var) {
        for(String primitiveLocation : PRIMITIVE_MEMORY_LOCATIONS) {
            PyBoolean isObject = (PyBoolean) interactiveConsole.eval("isinstance("+ var + ", " + primitiveLocation +  ")");
            if(isObject.getBooleanValue()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void set(String var, PyObject value) {
        interactiveConsole.set(var, value);
    }

    @Override
    public PyObject get(String var) {
        return interactiveConsole.get(var);
    }

    @Override
    public void setLocalsTo(PyObject locals) {
        interactiveConsole = new InteractiveConsole();
        interactiveConsole.setLocals(locals);
    }

    @Override
    public PyObject getLocals() {
        return interactiveConsole.getLocals();
    }
}
