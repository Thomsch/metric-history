package ch.thomsch.storage;

import ch.thomsch.model.ClassStore;

/**
 * The repository is located in the memory.
 */
public class FileSource extends MeasureRepository {
    private final ClassStore model;

    FileSource(ClassStore model) {
        this.model = model;
    }

    @Override
    public ClassStore get(String ... versions) {
        return model;
    }
}
