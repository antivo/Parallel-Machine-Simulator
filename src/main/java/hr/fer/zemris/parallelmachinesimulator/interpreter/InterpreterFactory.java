package hr.fer.zemris.parallelmachinesimulator.interpreter;

import hr.fer.zemris.parallelmachinesimulator.model.ActiveMemoryModel;
import hr.fer.zemris.parallelmachinesimulator.model.MemoryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by antivo
 */
@Component
public class InterpreterFactory {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ActiveMemoryModel memoryModel;

    public Interpreter createInterpreter() {
        Interpreter interpreter;
        if(memoryModel.getMemoryModel() == MemoryModel.RAM) {
            interpreter = applicationContext.getBean(PythonInterpreter.class);
        } else {
            interpreter = applicationContext.getBean(PRAMInterpreter.class);
        }
        return interpreter;
    }
}
