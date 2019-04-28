package org.metrichistory.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import org.metrichistory.model.MeasureStore;

/**
 * Encapsulate measures for a project from the file system.
 */
public abstract class MeasureRepository {

    private static final Logger logger = LoggerFactory.getLogger(MeasureRepository.class);

    /**
     * Builds a new instance of {@link MeasureRepository} depending of the source.
     * @param sourcePath the path on disk of the file or folder
     * @return the instance containing the data
     * @throws IOException if the data cannot be accessed
     */
    public static MeasureRepository build(String sourcePath) throws IOException {
        final File source = new File(sourcePath);

        if(source.isFile()) {
            final MeasureStore model = new MeasureStore();

            logger.info("Loading {}...", source.getPath());
            Stores.loadClasses(source, model);
            return new FileSource(model);
        } else {
            return new FolderSource(source);
        }
    }

    /**
     * Returns a {@link MeasureStore} populated with the artifacts of the given versions.
     * @param versions the versions to load into the class store
     * @return the new instance of {@link MeasureStore}
     * @throws IOException when the underlying data container fails
     */
    public abstract MeasureStore get(String ... versions) throws IOException;
}
