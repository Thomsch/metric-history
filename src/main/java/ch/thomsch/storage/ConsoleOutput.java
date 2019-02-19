package ch.thomsch.storage;

import java.util.Arrays;

import ch.thomsch.model.MeasureStore;

/**
 * Prints the results on the standard output.
 */
public class ConsoleOutput implements StoreOutput {

    @Override
    public void export(MeasureStore data) {

        System.out.println("ClassStore:");
        for (String version : data.versions()) {
            System.out.println(version + " -> " + Arrays.toString(data.artifacts(version).toArray()));
        }
    }
}
