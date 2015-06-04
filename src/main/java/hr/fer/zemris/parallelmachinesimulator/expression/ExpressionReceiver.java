package hr.fer.zemris.parallelmachinesimulator.expression;

import hr.fer.zemris.parallelmachinesimulator.exception.MemoryViolation;
import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.interpreter.PythonInterpreter;
import hr.fer.zemris.parallelmachinesimulator.memory.JointMemory;
import hr.fer.zemris.parallelmachinesimulator.memory.Memory;
import hr.fer.zemris.parallelmachinesimulator.memory.MemoryFactory;
import hr.fer.zemris.parallelmachinesimulator.model.ActiveMemoryModel;
import hr.fer.zemris.parallelmachinesimulator.model.MemoryModel;
import hr.fer.zemris.parallelmachinesimulator.output.VerboseComponent;
import hr.fer.zemris.parallelmachinesimulator.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by antivo
 */
@Component
public class ExpressionReceiver {
    private static final Character[] _operators = {'+', '*', '-', '/', '%', '!', '<', '>', '&', '|', '^', '~', '='};
    private static final HashSet<Character> OPERATORS = new HashSet<Character>(Arrays.asList(_operators));

    private Set<String> readLocationsAcc = new HashSet<>();
    private Set<String> writeLocationsAcc = new HashSet<>();
    private Set<String> ignoreLocationAcc = new HashSet<>();

    @Autowired
    private PythonInterpreter pythonInterpreter;

    @Autowired
    private JointMemory jointMemory;

    @Autowired
    private VerboseComponent verboseComponent;

    @Autowired
    private MemoryFactory memoryFactory;

    @Autowired
    private ActiveMemoryModel activeMemoryModel;

    private boolean isExclusiveWriting() {
        return (activeMemoryModel.getMemoryModel() == MemoryModel.EREW || activeMemoryModel.getMemoryModel() == MemoryModel.CREW);
    }

    private boolean isExclusiveReading() {
        return (activeMemoryModel.getMemoryModel() == MemoryModel.EREW || activeMemoryModel.getMemoryModel() == MemoryModel.ERCW);
    }

    private void obtainedLHS(String writeLocation) throws MemoryViolation {
        writeLocationsAcc.add(writeLocation);
        if(isExclusiveWriting()) {
            Memory writeMemory = memoryFactory.createMemory(writeLocation);
            jointMemory.writingToLocation(writeMemory);
        }
    }

    private void obtainedRHS(Set<String> readLocations) throws MemoryViolation {
        readLocationsAcc.addAll(readLocations);
        if(isExclusiveReading()) {
            Set<Memory> readMemories = readLocations.stream().map(location -> memoryFactory.createMemory(location)).collect(Collectors.toSet());
            jointMemory.readingFromLocation(readMemories);
        }
    }

    private void obtainedIL(String il) {
        ignoreLocationAcc.add(il);
        if(activeMemoryModel.getMemoryModel() != MemoryModel.CRCW) {
            Memory ignoreMemory = memoryFactory.createMemory(il);
            jointMemory.ignoreLocation(ignoreMemory);
        }
    }

    public void receiveIgnoreLocation(String location) throws SyntaxException, MemoryViolation {
        if(location.chars().allMatch(i-> Character.isLetter(i) || Character.isDigit(i) || i == '_')) {//  !location.contains("[") && !location.contains(".")) {
            assertNoFunctionCalls(location);
            location = location.replaceAll("\\s+", "");
            obtainedIL(location);
        }
    }

    public void receiveRHS(String rhs) throws SyntaxException, MemoryViolation {
        _receiveRHS(rhs);
    }

    public void receiveLHS(String lhs) throws SyntaxException, MemoryViolation {
        assertNoFunctionCalls(lhs);
        lhs = lhs.replaceAll("[)(]", "");
        lhs = calculateValues(lhs, true);
        lhs = lhs.replaceAll("\\s+", "");
        obtainedLHS(lhs);
    }

    public void reset() {
        ignoreLocationAcc.clear();
        readLocationsAcc.clear();
        writeLocationsAcc.clear();
    }

    public void nextNode() {
        if(ignoreLocationAcc.size() > 0) {
            verboseComponent.info("--------IL: " + StringUtils.concatenateWithComma(ignoreLocationAcc));
            ignoreLocationAcc.clear();
        }
        if(readLocationsAcc.size() > 0) {
            verboseComponent.info("--------RL: " + StringUtils.concatenateWithComma(readLocationsAcc));
            readLocationsAcc.clear();
        }
        if(writeLocationsAcc.size() > 0) {
            verboseComponent.info("--------WL: " + StringUtils.concatenateWithComma(writeLocationsAcc));
            writeLocationsAcc.clear();
        }
        jointMemory.nextNode();
    }

    private void _receiveRHS(String rhs) throws SyntaxException, MemoryViolation {
        if(!rhs.trim().equals("")) {
            /// hot fix, python does not support &&, ||, !
            rhs = rhs.replaceAll("==", "+");
            rhs = rhs.replaceAll(" and ", "+");
            rhs = rhs.replaceAll(" or ", "+");
            rhs = rhs.replaceAll("not ", "");
            Set<String> rhss = getRLocationsFromRHS(rhs);
            obtainedRHS(rhss);
        }
    }

    private Set<String> getRLocationsFromRHS(String rhs) throws SyntaxException, MemoryViolation {
        assertNoFunctionCalls(rhs);
        rhs = rhs.replaceAll("[)(]", ""); //B[i]==1 && B[i-1]==0
        rhs = calculateValues(rhs, true);
        Set<String> dividedByOperators = divideByOperators(rhs);
        for(String r : dividedByOperators) {
            assertValid(r);
        }
        Set<String> filteredLocations = dividedByOperators.stream().filter(ss -> isReadingLocation(ss)).collect(Collectors.toSet());
        Set<String> removedWhitespace = filteredLocations.stream().map(ss -> ss.replaceAll("\\s+", "")).collect(Collectors.toSet());
        return removedWhitespace;
    }

    private void assertValid(String rhs) throws SyntaxException {
        try {
            pythonInterpreter.eval(rhs);
        } catch(Exception e) {
            // NOT SYNTAX EXCEPTION ..
            throw new SyntaxException("Probably index out of range. Not able to determine error. Around '" + rhs + "'");
        }
    }

    private boolean isReadingLocation(String location) {
        return !location.equals("null") && !pythonInterpreter.eval(location).toString().equals(location);
    }

    private Set<String> divideByOperators(String rhs) {
        Set<String> locations = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rhs.length(); ++i) {
            final char c = rhs.charAt(i);
            if(OPERATORS.contains(c)) {
                String ss = sb.toString().trim();
                if(ss.length() > 0) {
                    locations.add(ss);
                }
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        String ss = sb.toString().trim();
        if(ss.length() > 0) {
            locations.add(ss);
        }
        return locations;
    }

    private String calculateValues(String rhs,  final boolean containsRHS) throws SyntaxException, MemoryViolation {
        StringBuilder sb = new StringBuilder();
        int counter = 0, beg = 0;
        for(int i = 0; i < rhs.length(); ++i) {
            char c = rhs.charAt(i);
            if ('[' == c) {
                if(0 == counter) {
                    beg = i;
                }
                ++counter;
            } else if(']' == c) {
                --counter;
                if(0 == counter) {
                    String contained = rhs.substring(beg + 1, i);
                    if(isIntValue(contained)) {
                        sb.append('[').append(getIntValue(contained)).append(']');
                        if(containsRHS) {
                            _receiveRHS(contained);
                        }
                    }
                    // else it must be list || tupple, or throw ...
                }
            } else if(0 == counter) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private boolean isIntValue(String expr) {
        try {
            Integer.parseInt(pythonInterpreter.eval(expr).toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private int getIntValue(String expr) {
        return Integer.parseInt(pythonInterpreter.eval(expr).toString());
    }

    private static void assertNoFunctionCalls(String rhs) throws SyntaxException  {
        for(int i = 1; i < rhs.length(); ++i) {
            if(rhs.charAt(i) == '(') {
                char previousChar = rhs.charAt(i - 1);
                if(Character.isLetterOrDigit(previousChar) ||  '_' ==previousChar) {
                    throw SyntaxException.functionsCall(rhs);
                }
            }
        }
    }
}
