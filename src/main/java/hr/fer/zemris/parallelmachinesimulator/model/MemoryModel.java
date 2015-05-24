package hr.fer.zemris.parallelmachinesimulator.model;

import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;

/**
 * Created by antivo
 */
public enum MemoryModel {
    RAM, EREW, ERCW, CREW, CRCW;

    public static MemoryModel fromString(String model) throws SyntaxException {
        switch (model.toUpperCase()) {
            case "RAM": return MemoryModel.RAM;
            case "EREW": return MemoryModel.EREW;
            case "ERCW": return MemoryModel.ERCW;
            case "CREW": return MemoryModel.CREW;
            case "CRCW": return MemoryModel.CRCW;
            default: throw new SyntaxException("Not a pram model. Model: '" + model + "'");
        }
    }
}
