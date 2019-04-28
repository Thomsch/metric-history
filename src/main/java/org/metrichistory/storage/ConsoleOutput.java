package org.metrichistory.storage;

import org.metrichistory.model.MeasureStore;

import java.util.Arrays;

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
