package org.metrichistory.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.metrichistory.storage.loader.CommitReader;

public class RevisionRepo {
    private static final Logger logger = LoggerFactory.getLogger(RevisionRepo.class);

    private final CommitReader commitReader;

    public RevisionRepo(CommitReader commitReader) {
        this.commitReader = commitReader;
    }

    public List<String> load(String file) {
        logger.info("Loading {}", file);
        return commitReader.make(file);
    }
}
