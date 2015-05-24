package hr.fer.zemris.parallelmachinesimulator.memory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by antivo
 */
@Component
public class MemoryFactory {
    @Autowired
    private ApplicationContext applicationContext;

    public Memory createMemory(String location) {
        Memory memory = applicationContext.getBean(Memory.class);
        memory.setLocation(location);
        return memory;
    }
}
