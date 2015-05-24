package hr.fer.zemris.parallelmachinesimulator.model;

import org.springframework.stereotype.Component;

/**
 * Created by antivo
 */
@Component
public class ActiveMemoryModel {
    private MemoryModel memoryModel = MemoryModel.RAM;

    public MemoryModel getMemoryModel() {
        return memoryModel;
    }

    public void setMemoryModel(MemoryModel memoryModel) {
        this.memoryModel = memoryModel;
    }
}
