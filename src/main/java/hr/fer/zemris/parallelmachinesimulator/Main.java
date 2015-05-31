package hr.fer.zemris.parallelmachinesimulator;

import hr.fer.zemris.parallelmachinesimulator.utils.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by antivo
 */
public class Main {
    private static Main instance;

    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    private void startPMS() {
        //PropertyConfigurator.configure(getClass().getResourceAsStream("/log4j.properties"));
        //Logger.getRootLogger().removeAllAppenders();
        ApplicationContext ctx =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        ParallelMachineSimulator pms = ctx.getBean(ParallelMachineSimulator.class);
        pms.run();
    }

    public static void main( String[] args ) throws FileNotFoundException {
        getInstance().startPMS();
    }
}