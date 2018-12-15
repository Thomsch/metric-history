package ch.thomsch.storage;

import java.util.Arrays;
import java.util.HashMap;

import ch.thomsch.model.ClassStore;
import ch.thomsch.model.Metrics;

/**
 * Prints the results on the standard output.
 */
public class ConsoleOutput implements TradeoffOutput {

    @Override
    public void export(ClassStore data) {

        System.out.println("ClassStore:");
        for (String version : data.getVersions()) {
            System.out.println(version + " -> " + Arrays.toString(data.getClasses(version).toArray()));
        }
    }
}
