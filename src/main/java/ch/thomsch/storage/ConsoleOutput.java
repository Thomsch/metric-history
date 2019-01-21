package ch.thomsch.storage;

import java.util.Arrays;

import ch.thomsch.model.ClassStore;

/**
 * Prints the results on the standard output.
 */
public class ConsoleOutput implements StoreOutput {

    @Override
    public void export(ClassStore data) {

        System.out.println("ClassStore:");
        for (String version : data.getVersions()) {
            System.out.println(version + " -> " + Arrays.toString(data.getClasses(version).toArray()));
        }
    }
}
