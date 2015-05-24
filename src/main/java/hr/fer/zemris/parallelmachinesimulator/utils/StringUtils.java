package hr.fer.zemris.parallelmachinesimulator.utils;

import java.util.Collection;
import java.util.List;

/**
 * Created by antivo
 */
public class StringUtils {

    public static String[] cut(String ss, int fromIncluded, int toIncluded) {
        String[] rez = new String[2];
        rez[0] = ss.substring(0, fromIncluded);
        rez[1] = ss.substring(toIncluded + 1, ss.length());
        return rez;
    }

    public static String concatenate(List<String> xs) {
        StringBuilder sb = new StringBuilder();
        xs.forEach(ss -> sb.append(ss));
        return sb.toString();
    }

    public static String concatenateWithComma(Collection<String> collection) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(String ss : collection){
            if(!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            sb.append(ss);
        }
        return sb.toString();
    }

    public static int calculateIndentation(String ss) {
        int count = 0;
        for(char c : ss.toCharArray()) {
            if(' ' != c) break;
            ++count;
        }
        return count;
    }
}
