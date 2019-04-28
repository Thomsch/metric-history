package org.metrichistory.storage;

import org.metrichistory.model.MeasureStore;

/**
 * The repository is located in the memory.
 */
public class FileSource extends MeasureRepository {
    private final MeasureStore model;

    FileSource(MeasureStore model) {
        this.model = model;
    }

    @Override
    public MeasureStore get(String ... versions) {
        return model;
    }
}
