package hr.fer.zemris.parallelmachinesimulator.memory;

import hr.fer.zemris.parallelmachinesimulator.interpreter.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by antivo
 */
@Component
@Scope("prototype")
public class Memory {
    private String location;

    @Autowired
    private PythonInterpreter pythonInterpreter;

    private boolean isAlias(Memory anotherMemory) {
        return pythonInterpreter.is(this.getLocation(), anotherMemory.getLocation());
    }

    private boolean isPrimitiveLocation() {
        return pythonInterpreter.isPrimitiveLocation(getLocation());
    }

    private static boolean equalLocations(Memory mem1, Memory mem2) {
        return mem1.getLocation().equals(mem2.getLocation());
    }

    public boolean isSubSet(Memory anotherMemory) {
        int thisLocationLength = this.getLocation().length();
        int anotherLocationLength = anotherMemory.getLocation().length();
        if(thisLocationLength - anotherLocationLength > 1) {
            return this.getLocation().startsWith(anotherMemory.getLocation());
        }
        return false;
    }

    public boolean isIntersected(Memory anotherMemory) {
        int thisLocationLength = this.getLocation().length();
        int anotherLocationLength = anotherMemory.getLocation().length();
        if(Math.abs(thisLocationLength - anotherLocationLength) > 1) {
            String longLocation;
            String shortLocation;
            if(thisLocationLength > anotherLocationLength) {
                longLocation = this.getLocation();
                shortLocation = anotherMemory.getLocation();
            } else {
                longLocation = anotherMemory.getLocation();
                shortLocation = this.getLocation();
            }
            String base = longLocation.substring(0, shortLocation.length() + 1);
            String candidateOne = shortLocation + ".";
            String candidateTwo = shortLocation + "[";
            return (base.equals(candidateOne) || base.equals(candidateTwo));
        }
        return false;
    }

    public static void equalize(Memory m1, Memory m2) {
        if(m1.getLocation().length() < m2.getLocation().length()) {
            m2.setLocation(m1.getLocation());
        } else {
            m1.setLocation(m2.getLocation());
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean equalOrSubset = false;
        if (this == obj) { // this should be impossible scenario
            equalOrSubset = true;
        }
        if (obj instanceof Memory) {
            Memory anotherMemory = (Memory) obj;
            if((pythonInterpreter.get(this.getLocation()) ==null || pythonInterpreter.get(anotherMemory.getLocation()) ==null) || this.isPrimitiveLocation() || anotherMemory.isPrimitiveLocation()) {
                equalOrSubset = equalLocations(this, anotherMemory) || this.isIntersected(anotherMemory);
            } else {
                equalOrSubset = isAlias(anotherMemory) || isIntersected(anotherMemory);
            }
        }
        return equalOrSubset;
    }

    @Override
    public String toString() {
         return "Memory.Location: " + location.toString();
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
