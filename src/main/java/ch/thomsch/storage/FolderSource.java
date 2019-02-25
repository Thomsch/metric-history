package ch.thomsch.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import ch.thomsch.model.MeasureStore;

/**
 * Reads the contents of a directory. Each file in the folder represents a revision.
 */
public class FolderSource extends MeasureRepository {

    private static final Logger logger = LoggerFactory.getLogger(FolderSource.class);
    private final File directory;

    FolderSource(File directory) {
        this.directory = directory;
    }

    @Override
    public MeasureStore get(String ... versions) throws IOException{
        final MeasureStore model = new MeasureStore();

        for (String version : versions) {
            final File file = new File(directory, version + ".csv");
            Stores.loadClasses(file.getPath(), model);
        }
        return model;
    }
}
