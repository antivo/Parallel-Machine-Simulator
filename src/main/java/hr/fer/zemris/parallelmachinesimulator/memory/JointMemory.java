package hr.fer.zemris.parallelmachinesimulator.memory;

import hr.fer.zemris.parallelmachinesimulator.exception.MemoryViolation;
import hr.fer.zemris.parallelmachinesimulator.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by antivo
 */
@Component
public class JointMemory  {
    private Set<Memory> readingAccessed = new HashSet<>();
    private Set<Memory> writingAccessed = new HashSet<>();

    private Set<Memory> readPerNode = new HashSet<>();
    private Set<Memory> writePerNode = new HashSet<>();
    private Set<Memory> ignorePerNode = new HashSet<>();


    public void reset() {
        readingAccessed = new HashSet<>();
        writingAccessed = new HashSet<>();

        readPerNode = new HashSet<>();
        writePerNode = new HashSet<>();
        ignorePerNode = new HashSet<>();
    }

    public void nextNode() {
        readingAccessed.addAll(readPerNode);
        readPerNode = new HashSet<>();

        writingAccessed.addAll(writePerNode);
        writePerNode = new HashSet<>();

        ignorePerNode = new HashSet<>();
    }

    public void readingFromLocation(Set<Memory> memories) throws MemoryViolation {
        memories.removeAll(ignorePerNode);
        if(!Collections.disjoint(memories, readingAccessed)) {
            memories.retainAll(readingAccessed);
            Set<String> locations = memories.stream().map(m -> m.getLocation()).collect(Collectors.toSet());
            throw new MemoryViolation("Shared memory reading constraint violation. Multiple reading from memory locations: " + StringUtils.concatenateWithComma(locations));
        } else {
            readPerNode.addAll(memories);
        }
    }

    public void writingToLocation(Memory memory) throws MemoryViolation {
        if(!ignorePerNode.contains(memory)) {
            if (writingAccessed.contains(memory)) {
                throw new MemoryViolation("Shared memory reading constraint violation. Multiple writing to memory locations: " + memory.getLocation());
            } else {
                writePerNode.add(memory);
            }
        }
    }

    public void ignoreLocation(Memory memory) {
        ignorePerNode.add(memory);
    }
}
