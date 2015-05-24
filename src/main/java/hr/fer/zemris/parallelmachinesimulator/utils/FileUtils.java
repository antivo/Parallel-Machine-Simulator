package hr.fer.zemris.parallelmachinesimulator.utils;

import org.python.core.PyFile;
import org.python.core.PyList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by antivo
 */
public class FileUtils {
    public static List<String> readScript(String path) throws FileNotFoundException {
        PyFile file = new PyFile(new FileInputStream(new File(path)));
        PyList lines = (PyList) file.readlines();
        return (List<String>)lines;
    }
}
