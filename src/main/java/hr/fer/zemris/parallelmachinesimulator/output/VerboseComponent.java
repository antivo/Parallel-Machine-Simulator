package hr.fer.zemris.parallelmachinesimulator.output;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by antivo
 */
@Component
public class VerboseComponent {
    private AtomicBoolean verbosity = new AtomicBoolean(false);

    public void setVerbosity(boolean verbose) {
        this.verbosity.set(verbose);
        System.out.println("Verbosity set to: " + verbose);
    }

    public void info(String ss) {
        if(verbosity.get()) {
            System.out.println(ss);
        }
    }
}
