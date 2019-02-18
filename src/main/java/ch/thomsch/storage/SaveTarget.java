package ch.thomsch.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import ch.thomsch.model.ClassStore;

/**
 * Encapsulate the location for saving data on the file system.
 */
public abstract class SaveTarget {

    private static final Logger logger = LoggerFactory.getLogger(SaveTarget.class);

    public static SaveTarget build(String destination) {
        if(DiskUtils.isFile(destination)) {
            return new FileTarget(new File(destination));
        } else {

            try {
                final File outputDir = DiskUtils.createDirectory(destination);
                return new FolderTarget(outputDir);
            } catch (IOException e) {
                logger.error("{} is not a valid output destination", destination, e);
                return null;
            }
        }
    }

    public abstract void export(ClassStore classStore);
}
