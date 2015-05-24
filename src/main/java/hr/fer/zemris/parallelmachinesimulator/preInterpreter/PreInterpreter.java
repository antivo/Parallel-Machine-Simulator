package hr.fer.zemris.parallelmachinesimulator.preInterpreter;

import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.preInterpreter.directive.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by antivo
 */
@Component
public class PreInterpreter implements PreInterpreterDirective {

    private List<PreInterpreterDirective> directives;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    private void init() {
        directives = new ArrayList<>();
        directives.add(applicationContext.getBean(Comment.class));
        directives.add(applicationContext.getBean(Verbose.class));
        directives.add(applicationContext.getBean(Load.class));
        directives.add(applicationContext.getBean(PRAM.class));
        directives.add(applicationContext.getBean(Reset.class));
    }

    @Override
    public boolean process(String line) throws SyntaxException {
        for (PreInterpreterDirective directive : directives) {
            if(directive.canProcess(line)) {
                return directive.process(line);
            }
        }
        throw new SyntaxException("Can not be processed by pre-interpreter. Line: '" + line + "'");
    }

    @Override
    public boolean canProcess(String line) {
        for (PreInterpreterDirective directive : directives) {
            if(directive.canProcess(line)) {
                return true;
            }
        }
        return false;
    }
}
