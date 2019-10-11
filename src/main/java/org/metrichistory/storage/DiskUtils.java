package org.metrichistory.storage;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public final class DiskUtils {

    private static final Logger logger = LoggerFactory.getLogger(DiskUtils.class);

    private DiskUtils() {
    }

    /**
     * Creates a new directory if it doesn't exists
     * @param output the path of the directory
     * @return a new instance of {@link File} representing the path of the directory
     * @throws IOException when the directory cannot be created
     */
    public static File createDirectory(String output) throws DirectoryCreationException {
        final File outputDir = new File(output);
        if (!outputDir.exists()) {
            logger.info("Creating folder {}", outputDir);
            final boolean success = outputDir.mkdir();
            if (!success) {
                throw new DirectoryCreationException(outputDir.toString());
            }
        }
        return outputDir;
    }

    public static String normalizePath(String arg) {
        return FilenameUtils.normalize(new File(arg).getAbsolutePath());
    }

}
