package org.metrichistory.storage;

import org.metrichistory.model.MeasureStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Exports results to a CSV file.
 */
public class CsvOutput implements StoreOutput {
    private static final Logger logger = LoggerFactory.getLogger(CsvOutput.class);

    private final String file;

    CsvOutput(String file) {
        this.file = file;
    }

    @Override
    public void export(MeasureStore data) {
        final FileTarget output = new FileTarget(new File(file));
        output.export(data);
    }
}
