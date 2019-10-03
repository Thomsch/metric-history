package org.metrichistory.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class RevisionFile {
    private static final Logger logger = LoggerFactory.getLogger(RevisionFile.class);

    private final CommitReader commitReader;

    public RevisionFile(CommitReader commitReader) {
        this.commitReader = commitReader;
    }

    public List<String> load(String file) throws IOException {
        logger.info("Loading {}", file);
        return commitReader.make(file);
    }
}
