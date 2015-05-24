package hr.fer.zemris.parallelmachinesimulator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileNotFoundException;

/**
 * Created by antivo
 */
public class Main {
    private static void startPMS() {
        ApplicationContext ctx =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        ParallelMachineSimulator pms = ctx.getBean(ParallelMachineSimulator.class);
        pms.run();
    }

    public static void main( String[] args ) throws FileNotFoundException {
        startPMS();
    }
}