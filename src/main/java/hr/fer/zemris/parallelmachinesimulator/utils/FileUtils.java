package hr.fer.zemris.parallelmachinesimulator.utils;

import org.python.core.PyFile;
import org.python.core.PyList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by antivo
 */
public class FileUtils {
    private static FileUtils instance;

    public static FileUtils getInstance() {
        if (instance == null) {
            instance = new FileUtils();
        }
        return instance;
    }

    public static List<String> loadScript(String path) throws FileNotFoundException {
        PyFile file = new PyFile(new FileInputStream(new File(path)));
        PyList lines = (PyList) file.readlines();
        return (List<String>)lines;
    }

    public List<String> readScript(String path) throws FileNotFoundException {
        InputStream inputStream = getClass().getResourceAsStream("/" + path);
        PyFile file = new PyFile(inputStream);
        PyList lines = (PyList) file.readlines();
        return (List<String>)lines;
    }
}
