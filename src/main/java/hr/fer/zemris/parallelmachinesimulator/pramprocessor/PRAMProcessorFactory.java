package hr.fer.zemris.parallelmachinesimulator.pramprocessor;

import hr.fer.zemris.parallelmachinesimulator.exception.SyntaxException;
import hr.fer.zemris.parallelmachinesimulator.pramprocessor.statement.Assignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by antivo
 */
@Component
public class PRAMProcessorFactory {
    @Autowired
    private ApplicationContext applicationContext;

    private Map<String, Class<? extends PRAMProcessor>> processorCatalogue = new HashMap<>();

    @PostConstruct
    private void init() {
        try {
            ClassPathScanningCandidateComponentProvider scanner =
                    new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(PRAMProcessorStatement.class));
            for (BeanDefinition bd : scanner.findCandidateComponents("hr.fer.zemris")) {
                Class<? extends PRAMProcessor> clazz = (Class<? extends PRAMProcessor>) Class.forName(bd.getBeanClassName());
                String keyword = AnnotationUtils.findAnnotation(clazz, PRAMProcessorStatement.class).keyword();
                processorCatalogue.put(keyword, clazz);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public PRAMProcessor createPRAMProcessor(String line, int calculatedIndentation, PRAMProcessor parent) throws SyntaxException {
        PRAMProcessor processor = applicationContext.getBean(selectFromCatalogue(line));
        processor.assign(line, calculatedIndentation, parent);
        return processor;
    }

    private Class<? extends PRAMProcessor> selectFromCatalogue(String line) throws SyntaxException {
        List<String> processors = processorCatalogue.keySet().stream().filter(ss -> line.contains(ss)).collect(Collectors.toList());
        if (1 == processors.size()) {
            return processorCatalogue.get(processors.get(0));
        } else if (0 == processors.size()) {
            throw new SyntaxException("Unknown statement. Can not process line: '" + line + "'");
        } else {
            if(2 == processors.size()) {
                for(String select : processors) {
                    if(!select.equals(Assignment.KEYWORD)) {
                        return processorCatalogue.get(select);
                    }
                }
            }
            StringBuilder sb = new StringBuilder();
            processors.forEach(s -> sb.append(s).append(','));
            String keywords = sb.substring(0, sb.length() - 1);
            throw new SyntaxException("More than one keyword(" + keywords + "). Can not process line: '" + line + "'");
        }
    }
}
