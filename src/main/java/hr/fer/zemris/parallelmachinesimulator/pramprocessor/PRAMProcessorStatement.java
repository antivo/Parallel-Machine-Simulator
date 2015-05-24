package hr.fer.zemris.parallelmachinesimulator.pramprocessor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by antivo
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PRAMProcessorStatement {
    String keyword();
}
