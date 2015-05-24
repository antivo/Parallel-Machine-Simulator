package hr.fer.zemris.parallelmachinesimulator.preInterpreter.directive;

import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.interpreter.ActiveInterpreter;
import hr.fer.zemris.parallelmachinesimulator.model.ActiveMemoryModel;
import hr.fer.zemris.parallelmachinesimulator.model.MemoryModel;
import hr.fer.zemris.parallelmachinesimulator.preInterpreter.PreInterpreterDirective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by antivo
 */
@Component
public class PRAM implements PreInterpreterDirective {
    private static final String KEYWORD = ":pram";

    @Autowired
    private ActiveMemoryModel activeMemoryModel;

    @Autowired
    private ActiveInterpreter activeInterpreter;

    @Override
    public boolean canProcess(String line) {
        if(!activeInterpreter.isActive() && getKeyword().length() <= line.length()) {
            for(int i = 0; i <getKeyword().length(); ++i) {
                if(getKeyword().charAt(i) != line.charAt(i)) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean process(String line) throws SyntaxException {
        String[] partsOfStatement = line.split(" ");
        //assert
        String model = partsOfStatement[1];
        activeMemoryModel.setMemoryModel(MemoryModel.fromString(model));
        System.out.println("PRAM model set to " + model);
        return false;
    }

    public String getKeyword() {
        return KEYWORD;
    }
}
