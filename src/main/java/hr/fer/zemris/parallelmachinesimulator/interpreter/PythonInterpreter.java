package hr.fer.zemris.parallelmachinesimulator.interpreter;

import org.python.core.PyObject;
import org.python.core.PyString;

/**
 * Created by antivo
 */
public interface PythonInterpreter extends Interpreter {
    public String raw_input(PyString prompt);

    public void writeErr(String data);

    public PyObject eval(String toEvaluate);

    public void exec(String toExecute);

    @Override
    public boolean push(String line);

    public boolean is(String var1, String var2);

    public boolean isPrimitiveLocation(String var);

    public void set(String var, PyObject value);

    public PyObject get(String var);

    public void setLocalsTo(PyObject locals);

    public PyObject getLocals();
}
