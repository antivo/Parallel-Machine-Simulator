package hr.fer.zemris.parallelmachinesimulator.memory;

import hr.fer.zemris.parallelmachinesimulator.exception.MemoryViolation;
import hr.fer.zemris.parallelmachinesimulator.output.VerboseComponent;
import hr.fer.zemris.parallelmachinesimulator.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private Set<Memory> ignorePermanent = new HashSet<>();

    private Set<Memory> readingAccessed = new HashSet<>();
    private Set<Memory> writingAccessed = new HashSet<>();

    private Set<Memory> readPerNode = new HashSet<>();
    private Set<Memory> writePerNode = new HashSet<>();
    private Set<Memory> ignorePerNode = new HashSet<>();

    @Autowired
    private VerboseComponent verboseComponent;

    public void reset() {
        restart();
        ignorePermanent.clear();
    }

    public void restart() {
        readingAccessed.clear();
        writingAccessed.clear();

        readPerNode.clear();
        writePerNode.clear();
        ignorePerNode.clear();
    }

    public void nextNode() {
        readingAccessed.addAll(readPerNode);
        readPerNode.clear();

        writingAccessed.addAll(writePerNode);
        writePerNode.clear();

        ignorePerNode.clear();
    }

    public void addPermanentIgnore(Memory memory) {
        ignorePermanent.add(memory);
        verboseComponent.info("--AI: " + memory.getLocation());
    }

    public void readingFromLocation(Set<Memory> memories) throws MemoryViolation {
        memories.removeAll(ignorePerNode);
        memories.removeAll(ignorePermanent);
        for(Memory ignored : ignorePermanent) {
            memories = memories.stream().filter(m -> !m.isSubSet(ignored)).collect(Collectors.toSet());
        }
        for(Memory ignored : ignorePerNode) {
            memories = memories.stream().filter(m -> !m.isSubSet(ignored)).collect(Collectors.toSet());
        }
        if(!Collections.disjoint(memories, readingAccessed)) {
            memories.retainAll(readingAccessed);
            Set<String> locations = memories.stream().map(m -> m.getLocation()).collect(Collectors.toSet());
            throw new MemoryViolation("Shared memory reading constraint violation. Multiple reading from memory locations: " + StringUtils.concatenateWithComma(locations));
        } else {
            readPerNode.addAll(memories);
        }
    }

    public void writingToLocation(Memory memory) throws MemoryViolation {
        boolean isSub = false;
        for(Memory ignored : ignorePerNode) {
            isSub &= memory.isSubSet(ignored);
            if(isSub) {
                break;
            }
        }
        if(!isSub && !ignorePermanent.contains(memory) && !ignorePerNode.contains(memory)) {
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
